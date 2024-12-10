package com.genericbadname.s2lib.mixin.entity;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.bakery.storage.Bakery;
import com.genericbadname.s2lib.config.CommonConfig;
import com.genericbadname.s2lib.network.S2NetworkingConstants;
import com.genericbadname.s2lib.network.S2NetworkingUtil;
import com.genericbadname.s2lib.pathing.AStarPathCalculator;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Path;
import com.genericbadname.s2lib.pathing.entity.S2SmartMob;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(Mob.class)
public abstract class MixinS2Mob extends LivingEntity implements S2SmartMob {

    @Shadow @Nullable public abstract LivingEntity getTarget();

    @Unique
    private static final int RETRY_UPDATE_COOLDOWN = 2; // ticks
    @Unique
    private static final int FAIL_UPDATE_PENALTY = 20; // ticks
    @Unique
    private int updateTimer = RETRY_UPDATE_COOLDOWN;
    @Unique
    private AStarPathCalculator calculator;
    @Unique
    private S2Path potentialPath;
    @Unique
    private BetterBlockPos lastTracked;

    protected MixinS2Mob(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);

        if (!level.isClientSide()) {
            this.calculator = new AStarPathCalculator();
            this.potentialPath = null;
            this.lastTracked = BetterBlockPos.ORIGIN;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (updateTimer > 0) updateTimer--;
    }

    @Override
    public void updatePath() {
        if (updateTimer != 0) return;

        LivingEntity target = this.getTarget();
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
                        S2Lib.LOGGER.error("Entity {} could not access its level.", this.getUUID());
                    }
                }, S2Lib.SERVICE);
            }
        }
    }

    @Override
    public S2Path getPotentialPath() {
        return potentialPath;
    }

    @Override
    public S2Path calculateFromCurrentLocation(BetterBlockPos dest) throws IOException {
        Bakery bakery;
        try (Level level = this.level()) {
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
            buf.writeUUID(this.uuid);
            potentialPath.serialize(buf);

            S2NetworkingUtil.dispatchTrackingEntity(S2NetworkingConstants.RENDER_MOB_PATH, buf, (Mob)(Object)this);
        }

        return potentialPath;
    }
}
