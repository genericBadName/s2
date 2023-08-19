package com.genericbadname.s2lib.example.goal;

import com.genericbadname.s2lib.pathing.AStarPathCalculator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

public class GetToBlockGoal extends Goal {
    private final Mob mob;
    private final BlockPos target;
    private List<BlockPos> path;

    private BlockPos walkingTo;

    public GetToBlockGoal(Mob mob, BlockPos target) {
        this.mob = mob;
        this.target = target;

        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void stop() {
        walkingTo = null;
        path = null;

        mob.getNavigation().stop();
    }

    @Override
    public void start() {
        path = new AStarPathCalculator(mob.blockPosition(), target, mob.level).calculate().getPositions();
        walkingTo = path.get(0);
    }

    @Override
    public void tick() {
        if (path == null) return;
        if (path.size() < 2) return;

        float yRot = (float) rotFromPos(mob.blockPosition(), path.get(1));

        if (mob.blockPosition().equals(walkingTo)) {
            path.remove(0);
            walkingTo = path.get(0);
            mob.lerpTo(walkingTo.getX()+0.5, walkingTo.getY(), walkingTo.getZ()+0.5, yRot, 0, 10, false);
            mob.setYHeadRot(yRot);
        }
    }

    // 4 or 8 directional movement
    private double rotFromPos(BlockPos pos1, BlockPos pos2) {
        int x = pos2.getX() - pos1.getX();
        int z = pos2.getZ() - pos1.getZ();

        return (x * -90) + (z < 0 ? 180 : 0);
    }
}
