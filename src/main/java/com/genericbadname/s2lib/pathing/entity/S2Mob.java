package com.genericbadname.s2lib.pathing.entity;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.config.CommonConfig;
import com.genericbadname.s2lib.network.S2NetworkingConstants;
import com.genericbadname.s2lib.network.S2NetworkingUtil;
import com.genericbadname.s2lib.pathing.AStarPathCalculator;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Path;
import com.google.common.util.concurrent.AtomicDouble;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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


    // ai ticking
    @Override
    public void tick() {
        super.tick();

        if (updateTimer > 0) updateTimer--;
    }

    private void handleLerp() {
        if (isControlledByLocalInstance()) {
            lerpSteps = 0;
            syncPacketPositionCodec(getX(), getY(), getZ());
        }

        if (lerpSteps > 0) {
            double x = getX() + (lerpX - getX()) / (double)lerpSteps;
            double y = getY() + (lerpY - getY()) / (double)lerpSteps;
            double z = getZ() + (lerpZ - getZ()) / (double)lerpSteps;
            double yr = Mth.wrapDegrees(lerpYRot - (double)getYRot());
            setYRot(getYRot() + (float)yr / (float)lerpSteps);
            setXRot(getXRot() + (float)(lerpXRot - (double)getXRot()) / (float)lerpSteps);
            --lerpSteps;
            setPos(x, y, z);
            setRot(getYRot(), getXRot());
        } else if (!isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }

        if (lerpHeadSteps > 0) {
            yHeadRot += (float)Mth.wrapDegrees(lyHeadRot - (double)yHeadRot) / (float)lerpHeadSteps;
            --lerpHeadSteps;
        }
    }

    private void handleThresholdVelocity() {
        Vec3 velocity = this.getDeltaMovement();
        double vx = velocity.x;
        double vy = velocity.y;
        double vz = velocity.z;
        if (Math.abs(vx) < 0.003D) {
            vx = 0.0D;
        }

        if (Math.abs(vy) < 0.003D) {
            vy = 0.0D;
        }

        if (Math.abs(vz) < 0.003D) {
            vz = 0.0D;
        }

        setDeltaMovement(vx, vy, vz);
    }

    private void handleAcceleration() {
        level.getProfiler().pop();
        level.getProfiler().push("travel");
        //updateFallFlying();
        LivingEntity controllingPassenger = getControllingPassenger();
        double drag = 0.02; // to be honest, I have no idea why this is here. it doesn't even do anything.

        xxa *= (1-drag);
        zza *= (1-drag);

        Vec3 velAfterDrag = new Vec3(xxa, yya, zza);

        if (controllingPassenger != null && isAlive()) {
            travelRidden(controllingPassenger, velAfterDrag);
            // handle that later...
        } else {
            travel(velAfterDrag);
        }

        this.level.getProfiler().pop();
    }

    private void handleFreezing() {
        level.getProfiler().pop();
        level.getProfiler().push("freezing");
        if (!level.isClientSide && !isDeadOrDying()) {
            int ticksFrozen = getTicksFrozen();
            if (isInPowderSnow && canFreeze()) {
                setTicksFrozen(Math.min(getTicksRequiredToFreeze(), ticksFrozen + 1));
            } else {
                setTicksFrozen(Math.max(0, ticksFrozen - 2));
            }
        }

        removeFrost();
        tryAddFrost();
        if (!level.isClientSide && tickCount % 40 == 0 && isFullyFrozen() && canFreeze()) {
            hurt(damageSources().freeze(), 1.0F);
        }
    }

    private void handlePushing() {
        level.getProfiler().pop();
        level.getProfiler().push("push");
        if (autoSpinAttackTicks > 0) {
            --autoSpinAttackTicks;
            checkAutoSpinAttack(getBoundingBox(), getBoundingBox());
        }
    }

    private void handleWaterDamage() {
        pushEntities();
        level.getProfiler().pop();
        if (!level.isClientSide && isSensitiveToWater() && isInWaterRainOrBubble()) {
            hurt(damageSources().drown(), 1.0F);
        }
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

    // path calculation
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
