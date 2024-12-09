package com.genericbadname.s2lib.pathing.entity;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.bakery.storage.Bakery;
import com.genericbadname.s2lib.config.CommonConfig;
import com.genericbadname.s2lib.network.S2NetworkingConstants;
import com.genericbadname.s2lib.network.S2NetworkingUtil;
import com.genericbadname.s2lib.pathing.AStarPathCalculator;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Path;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class S2Mob extends PathfinderMob {
    public static final int RETRY_UPDATE_COOLDOWN = 2; // ticks
    public static final int FAIL_UPDATE_PENALTY = 20; // ticks
    private int updateTimer = RETRY_UPDATE_COOLDOWN;
    private AStarPathCalculator calculator;
    private S2Path potentialPath;
    private BetterBlockPos lastTracked;

    public S2Mob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);

        if (!level.isClientSide()) {
            this.calculator = new AStarPathCalculator();
            this.potentialPath = null;
            this.lastTracked = BetterBlockPos.ORIGIN;
        }
    }


    // ai ticking
    @Override
    public void tick() {
        super.tick();

        if (updateTimer > 0) updateTimer--;
    }

    public void updatePath() {
        if (updateTimer != 0) return;

        LivingEntity target = getTarget();
        updateTimer = RETRY_UPDATE_COOLDOWN;
        potentialPath = null;

        // update path according to target position
        if (target != null) {
            if (!target.blockPosition().equals(lastTracked)) {
                S2Lib.logInfo("Updating path for {}", this);

                CompletableFuture.runAsync(() -> {
                    lastTracked = BetterBlockPos.from(target.blockPosition());
                    try {
                        potentialPath = calculateFromCurrentLocation(lastTracked);
                    } catch (IOException e) {
                        S2Lib.LOGGER.error("Entity {} could not access its level.", uuid);
                    }
                }, S2Lib.SERVICE);
            }
        }
    }

    // path calculation
    public S2Path getPotentialPath() {
        return potentialPath;
    }
    public S2Path calculateFromCurrentLocation(BetterBlockPos dest) throws IOException {
        Bakery bakery;
        try (Level level = level()) {
            bakery = ((ServerLevel) level).getServer().getBakery(level.dimension());
        }

        if (bakery == null) {
            S2Lib.LOGGER.warn("Entity {} tried to pathfind with a nonexistent bakery. If the world was just loaded, ignore this.", uuid);
            updateTimer = FAIL_UPDATE_PENALTY;

            return null;
        }


        potentialPath = calculator.calculate(BetterBlockPos.from(blockPosition()), dest, bakery);

        if (potentialPath == null) return potentialPath;
        if (!potentialPath.isPossible()) updateTimer = FAIL_UPDATE_PENALTY; // stop constant failure updates

        // send data to client for debug
        if (CommonConfig.DEBUG_ENTITY_PATHS.get()) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeUUID(uuid);
            potentialPath.serialize(buf);

            S2NetworkingUtil.dispatchTrackingEntity(S2NetworkingConstants.RENDER_MOB_PATH, buf, this);
        }

        return potentialPath;
    }

    // other
    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);

        S2NetworkingUtil.dispatchAll(S2NetworkingConstants.REMOVE_MOB_PATH, PacketByteBufs.create().writeUUID(uuid), getServer());
        S2NetworkingUtil.dispatchAll(S2NetworkingConstants.CLEAR_NODES, PacketByteBufs.empty(), getServer());
    }
}
