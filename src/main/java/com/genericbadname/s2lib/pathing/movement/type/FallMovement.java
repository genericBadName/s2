package com.genericbadname.s2lib.pathing.movement.type;

import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

public class FallMovement extends WalkMovement
{
    private double originalY = 1024;

    @Override
    public void move(Mob mob, BlockPos pos) {
        if (originalY == 1024) originalY = mob.getY();
        float yRot = (float) Moves.rotFromPos(mob.blockPosition(), pos);

        mob.lerpTo(pos.getX()+0.5, originalY, pos.getZ()+0.5, yRot, 0, 10, false);
        mob.setYHeadRot(yRot);
    }
}
