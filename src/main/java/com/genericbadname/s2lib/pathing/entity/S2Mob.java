package com.genericbadname.s2lib.pathing.entity;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.config.CommonConfig;
import com.genericbadname.s2lib.network.S2NetworkingConstants;
import com.genericbadname.s2lib.network.S2NetworkingUtil;
import com.genericbadname.s2lib.pathing.AStarPathCalculator;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Path;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import java.util.Optional;

public abstract class S2Mob extends PathfinderMob {
    public static final int RETRY_UPDATE_COOLDOWN = 20; // ticks
    public static final int FAIL_UPDATE_PENALTY = 60; // ticks
    private int updateTimer = RETRY_UPDATE_COOLDOWN;
    private final AStarPathCalculator calculator;
    private Optional<S2Path> potentialPath;
    private BetterBlockPos lastTracked;
    public S2Mob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.calculator = new AStarPathCalculator(level);
        this.potentialPath = Optional.of(new S2Path());
        this.lastTracked = BetterBlockPos.ORIGIN;
    }

    public AStarPathCalculator getCalculator() {
        return calculator;
    }

    @Override
    public void tick() {
        super.tick();

        if (updateTimer > 0) updateTimer--;
    }

    public Optional<S2Path> getPotentialPath() {
        return potentialPath;
    }
    public Optional<S2Path> calculateFromCurrentLocation(BetterBlockPos dest) {
        potentialPath = calculator.calculate(BetterBlockPos.from(blockPosition()), dest);

        if (potentialPath.isEmpty()) return potentialPath;
        S2Path path = potentialPath.get();

        if (!path.isPossible()) updateTimer = FAIL_UPDATE_PENALTY; // stop constant failure updates

        // send data to client for debug
        if (CommonConfig.DEBUG_ENTITY_PATHS.get()) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeUUID(uuid);
            path.serialize(buf);

            for (ServerPlayer player : PlayerLookup.tracking(this)) {
                ServerPlayNetworking.send(player, S2NetworkingConstants.RENDER_MOB_PATH, buf);
            }

            S2NetworkingUtil.dispatchTrackingEntity(S2NetworkingConstants.RENDER_MOB_PATH, buf, this);
        }

        return potentialPath;
    }

    public void updatePath() {
        if (updateTimer != 0) return;

        LivingEntity target = getTarget();
        updateTimer = RETRY_UPDATE_COOLDOWN;
        potentialPath = Optional.empty();

        // update path according to target position
        if (target != null) {
            if (!target.blockPosition().equals(lastTracked)) {
                S2Lib.logInfo("Updating path for {}", this);

                lastTracked = BetterBlockPos.from(target.blockPosition());
                potentialPath = calculateFromCurrentLocation(lastTracked);
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(uuid);

        S2NetworkingUtil.dispatchAll(S2NetworkingConstants.REMOVE_MOB_PATH, buf, getServer());
        S2NetworkingUtil.dispatchAll(S2NetworkingConstants.CLEAR_NODES, PacketByteBufs.empty(), getServer());
    }
}
