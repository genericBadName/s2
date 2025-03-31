package com.genericbadname.s2lib.mixin.entity;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.bakery.storage.Bakery;
import com.genericbadname.s2lib.config.CommonConfig;
import com.genericbadname.s2lib.network.S2NetworkingConstants;
import com.genericbadname.s2lib.network.S2NetworkingUtil;
import com.genericbadname.s2lib.network.packet.RenderNodeUpdateS2CPacket;
import com.genericbadname.s2lib.pathing.AStarPathCalculator;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Path;
import com.genericbadname.s2lib.pathing.entity.S2SmartMob;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Mob.class)
public abstract class MixinS2Mob implements S2SmartMob, Targeting {
    @Unique
    private static final int RETRY_UPDATE_COOLDOWN = 10; // ticks
    @Unique
    private static final int FAIL_UPDATE_PENALTY = 20; // ticks
    @Unique
    private final AtomicInteger updateTimer = new AtomicInteger(RETRY_UPDATE_COOLDOWN);
    @Unique
    private AStarPathCalculator calculator;
    @Unique
    private S2Path potentialPath;
    @Unique
    private BetterBlockPos lastTracked;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void constructor(EntityType<?> entityType, Level level, CallbackInfo ci) {
        if (!level.isClientSide()) {
            this.calculator = new AStarPathCalculator();
            this.potentialPath = null;
            this.lastTracked = BetterBlockPos.ORIGIN;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (updateTimer.get() > 0 && potentialPath == null) updateTimer.getAndDecrement();
    }

    @Override
    public void s2$updatePath() {
        if (updateTimer.get() != 0) return;

        LivingEntity target = this.getTarget();
        updateTimer.set(RETRY_UPDATE_COOLDOWN);
        potentialPath = null;

        // update path according to target position
        if (target != null) {
            if (!target.blockPosition().equals(lastTracked)) {
                S2Lib.logInfo("Updating path for {}", this);

                lastTracked = BetterBlockPos.from(target.blockPosition());
                s2$calculateFromCurrentLocation(lastTracked);
            }
        }
    }

    @Override
    public S2Path s2$getPotentialPath() {
        return potentialPath;
    }

    @Unique
    private synchronized void setPotentialPath(S2Path path) {
        potentialPath = path;
    }

    @Unique
    private Level getLevel() {
        return ((Mob)((Object)this)).level();
    }

    @Override
    public CompletableFuture<Void> s2$calculateFromCurrentLocation(BetterBlockPos dest) {
        Bakery bakery;
        Level level = getLevel();

        if (level != null) {
            bakery = ((ServerLevel) level).getServer().getBakery(level.dimension());
        }
        else {
            S2Lib.LOGGER.warn("Entity {} tried to pathfind with a nonexistent bakery. If the world was just loaded, ignore this.", ((EntityAccessor)this).getUuid());
            updateTimer.set(FAIL_UPDATE_PENALTY);

            return CompletableFuture.runAsync(() -> {});
        }

        BetterBlockPos src = BetterBlockPos.from(((EntityAccessor)this).getBlockPosition());
        // ONLY RUN CALCULATIONS ON ANOTHER THREAD! ACCESS LEVEL AND ENTITY BEFOREHAND!
        return CompletableFuture.supplyAsync(() -> calculator.calculate(src, dest, bakery), S2Lib.SERVICE)
                .thenAccept(path -> {
                    // send debug updates
                    for (Object2BooleanMap.Entry<BetterBlockPos> entry : calculator.nodeUpdates().object2BooleanEntrySet()) {
                        S2NetworkingUtil.dispatchAll(S2NetworkingConstants.RENDER_NODE_UPDATE, RenderNodeUpdateS2CPacket.create(entry.getKey(), entry.getBooleanValue()), level.getServer());
                    }

                    if (path == null) return;
                    if (!path.isPossible()) {
                        updateTimer.set(FAIL_UPDATE_PENALTY); // stop constant failure updates
                        return;
                    }

                    // send data to client for debug
                    if (CommonConfig.DEBUG_ENTITY_PATHS.get()) {
                        FriendlyByteBuf buf = PacketByteBufs.create();
                        buf.writeUUID(((EntityAccessor)this).getUuid());
                        path.serialize(buf);

                        S2NetworkingUtil.dispatchTrackingEntity(S2NetworkingConstants.RENDER_MOB_PATH, buf, (Mob)(Object)this);
                    }

                    S2Lib.logInfo("Valid path found for {}", this);
                    setPotentialPath(path);
                });
    }
}
