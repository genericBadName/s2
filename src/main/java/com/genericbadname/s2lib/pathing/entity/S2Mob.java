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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
        potentialPath = null;

        // update path according to target position
        if (target != null) {
            if (!target.blockPosition().equals(lastTracked)) {
                S2Lib.logInfo("Updating path for {}", this);

                CompletableFuture.runAsync(() -> {
                    lastTracked = BetterBlockPos.from(target.blockPosition());
                    potentialPath = calculateFromCurrentLocation(lastTracked);
                }, S2Lib.SERVICE);
            }
        }
    }

    // path calculation
    public S2Path getPotentialPath() {
        return potentialPath;
    }
    public S2Path calculateFromCurrentLocation(BetterBlockPos dest) {
        Bakery bakery = ((ServerLevel)level).getServer().getBakery(level.dimension());

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
