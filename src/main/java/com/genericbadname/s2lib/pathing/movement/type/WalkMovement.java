package com.genericbadname.s2lib.pathing.movement.type;

import com.genericbadname.s2lib.bakery.eval.BakedLevelAccessor;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import com.genericbadname.s2lib.pathing.movement.PositionValidity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

public class WalkMovement implements IMovement {
    @Override
    public void move(Mob mob, BlockPos pos) {
        float yRot = (float) Moves.rotFromPos(mob.blockPosition(), pos);

        mob.lerpTo(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, yRot, 0, 10, false);
        mob.setYHeadRot(yRot);
    }

    @Override
    public PositionValidity isValidPosition(BakedLevelAccessor bakery, BetterBlockPos pos) {
        if (!bakery.isPassable(pos)) return PositionValidity.FAIL_BLOCKED; // ensure foot is passable
        if (!bakery.isPassable(pos.offset(0, 1, 0))) return PositionValidity.FAIL_BLOCKED; // ensure head is passable
        if (!bakery.isWalkable(pos.offset(0, -1, 0))) return PositionValidity.FAIL_MISSING_BLOCK; // ensure stepping on block is possible

        return PositionValidity.SUCCESS;
    }
}
