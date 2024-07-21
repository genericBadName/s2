package com.genericbadname.s2lib.pathing.movement.type;

import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class WalkMovement implements IMovement {
    @Override
    public void move(Mob mob, BlockPos pos) {
        float yRot = (float) Moves.rotFromPos(mob.blockPosition(), pos);
        //mob.lerpTo(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, 0, 0, 1, true);
        mob.setPosRaw(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
        mob.setDeltaMovement(Vec3.ZERO);
        //mob.move(MoverType.SELF, new Vec3(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5).subtract(mob.position()));

        mob.setYRot(yRot);
        mob.setYHeadRot(yRot);
    }
}
