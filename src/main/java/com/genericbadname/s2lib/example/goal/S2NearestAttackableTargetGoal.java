package com.genericbadname.s2lib.example.goal;

import com.genericbadname.s2lib.pathing.S2Node;
import com.genericbadname.s2lib.pathing.S2Path;
import com.genericbadname.s2lib.pathing.entity.S2Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class S2NearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private S2Mob s2Mob;
    private S2Node nextNode;
    private List<S2Node> currentPath;
    private boolean movingAlongPath = false;
    public S2NearestAttackableTargetGoal(S2Mob mob, Class<T> targetType, boolean mustSee) {
        super(mob, targetType, mustSee);
    }

    public S2NearestAttackableTargetGoal(S2Mob mob, Class<T> targetType, boolean mustSee, Predicate<LivingEntity> targetPredicate) {
        super(mob, targetType, mustSee, targetPredicate);
    }

    public S2NearestAttackableTargetGoal(S2Mob mob, Class<T> targetType, boolean mustSee, boolean mustReach) {
        super(mob, targetType, mustSee, mustReach);
    }

    public S2NearestAttackableTargetGoal(S2Mob mob, Class<T> targetType, int randomInterval, boolean mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, targetType, randomInterval, mustSee, mustReach, targetPredicate);
    }

    @Override
    public void start() {
        super.start();
        s2Mob = (S2Mob) mob;

        if (s2Mob.getPotentialPath().isEmpty()) return;
        S2Path potentialPath = s2Mob.getPotentialPath().get();

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
    @Override
    public void tick() {
        super.tick();

        if (!movingAlongPath) attemptPathUpdate();

        if (currentPath == null) return;
        if (currentPath.size() < 2) {
            stop();
            return;
        }

        nextNode = currentPath.get(0);

        if (s2Mob.blockPosition().equals(nextNode.getPos())) {
            currentPath.remove(0);
            nextNode = currentPath.get(0);

            nextNode.getMove().type.move(mob, nextNode.getPos());
        }
    }

    private void attemptPathUpdate() {
        s2Mob.updatePath();
        if (s2Mob.getPotentialPath().isEmpty()) return;
        currentPath = s2Mob.getPotentialPath().get().getNodes();
        movingAlongPath = true;
    }

    @Override
    protected void findTarget() {
        super.findTarget();
    }
}
