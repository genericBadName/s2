package com.genericbadname.s2lib.example.goal;

import com.genericbadname.s2lib.pathing.S2Node;
import com.genericbadname.s2lib.pathing.S2Path;
import com.genericbadname.s2lib.pathing.entity.S2SmartMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class S2NearestAttackableTargetGoal<T extends LivingEntity, M extends Mob & S2SmartMob> extends NearestAttackableTargetGoal<T> {
    private M s2Mob;
    private S2Node nextNode;
    private List<S2Node> currentPath;
    private boolean movingAlongPath = false;
    public S2NearestAttackableTargetGoal(M mob, Class<T> targetType, boolean mustSee) {
        super(mob, targetType, mustSee);
    }

    public S2NearestAttackableTargetGoal(M mob, Class<T> targetType, boolean mustSee, Predicate<LivingEntity> targetPredicate) {
        super(mob, targetType, mustSee, targetPredicate);
    }

    public S2NearestAttackableTargetGoal(M mob, Class<T> targetType, boolean mustSee, boolean mustReach) {
        super(mob, targetType, mustSee, mustReach);
    }

    public S2NearestAttackableTargetGoal(M mob, Class<T> targetType, int randomInterval, boolean mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, targetType, randomInterval, mustSee, mustReach, targetPredicate);
    }

    @Override
    public void start() {
        super.start();

        if (! (mob instanceof S2SmartMob && mob instanceof Mob)) return;

        s2Mob = (M) mob;

        if (s2Mob.getPotentialPath() == null) return;
        S2Path potentialPath = s2Mob.getPotentialPath();

        if (potentialPath.isPossible()) {
            currentPath = potentialPath.getNodes();
            nextNode = currentPath.get(0);
        }
    }

    @Override
    public void stop() {
        super.stop();
        nextNode = null;
        currentPath = null;
        movingAlongPath = false;
    }

    // TODO: make this logic less hideous
    AtomicInteger stallTick = new AtomicInteger(0);
    boolean moveQueued = false;
    @Override
    public void tick() {
        super.tick();
        stallTick.getAndDecrement();

        if (!movingAlongPath) attemptPathUpdate();

        if (currentPath == null) return;
        if (currentPath.size() < 2) {
            stop();
            return;
        }

        nextNode = currentPath.get(0);

        if (s2Mob.blockPosition().equals(nextNode.getPos()) && s2Mob.position().y == nextNode.getPos().y) {
            currentPath.remove(0);
            nextNode = currentPath.get(0);

            mob.setPos(s2Mob.blockPosition().getX() + 0.5, s2Mob.blockPosition().getY(), s2Mob.blockPosition().getZ() + 0.5);

            stallTick.set(1);
            moveQueued = true;
        }

        if (stallTick.get() <= 0 && moveQueued) {
            nextNode.getMove().type.move(mob, nextNode.getPos());
            moveQueued = false;
        }
    }

    private void attemptPathUpdate() {
        s2Mob.updatePath();
        if (s2Mob.getPotentialPath() == null) return;
        currentPath = s2Mob.getPotentialPath().getNodes();
        movingAlongPath = true;
    }

    @Override
    protected void findTarget() {
        super.findTarget();
    }
}
