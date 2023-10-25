package com.genericbadname.s2lib.pathing.movement.type;

import com.genericbadname.s2lib.config.ServerConfig;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class VerticalMovement implements IMovement
{
    private final boolean ascending;
    private final int blocks;
    public VerticalMovement(boolean ascending, int blocks) {
        this.ascending = ascending;
        this.blocks = blocks;
    }
    @Override
    public void move(Mob mob, BlockPos pos) {
        float yRot = (float) Moves.rotFromPos(mob.blockPosition(), pos);

        mob.lerpTo(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, yRot, 0, 10, false);
        mob.setYHeadRot(yRot);
    }

    @Override
    public double cost(Mob mob, BlockPos start, BlockPos end) {
        return start.distSqr(end) + blocks * (ascending ? ServerConfig.ASCEND_COST.get() : ServerConfig.DESCEND_COST.get());
    }

    @Override
    public boolean isValidPosition(Level level, BetterBlockPos pos) {
        return false;
    }
}
