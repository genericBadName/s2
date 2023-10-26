package com.genericbadname.s2lib.pathing.movement.type;

import com.genericbadname.s2lib.bakery.eval.BakedLevelAccessor;
import com.genericbadname.s2lib.data.tag.ModBlockTags;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static com.genericbadname.s2lib.bakery.eval.BakedLevelAccessor.HazardLevel;

public class WalkMovement implements IMovement {
    @Override
    public void move(Mob mob, BlockPos pos) {
        float yRot = (float) Moves.rotFromPos(mob.blockPosition(), pos);

        mob.lerpTo(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, yRot, 0, 10, false);
        mob.setYHeadRot(yRot);
    }

    @Override
    public double cost(Mob mob, BlockPos start, BlockPos end) {
        return start.distSqr(end);
    }

    @Override
    public PositionValidity isValidPosition(BakedLevelAccessor bakery, BetterBlockPos pos) {
        if (bakery.isWalkable(pos)) return PositionValidity.FAIL_BLOCKED; // ensure foot is passable
        if (bakery.isWalkable(pos.offset(0, 1, 0))) return PositionValidity.FAIL_BLOCKED; // ensure head is passable
        if (bakery.isPassable(pos.offset(0, -1, 0))) return PositionValidity.FAIL_MISSING_BLOCK; // ensure stepping on block is possible

        return PositionValidity.SUCCESS;
    }
}
