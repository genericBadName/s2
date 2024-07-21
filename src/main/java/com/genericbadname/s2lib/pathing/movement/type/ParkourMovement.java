package com.genericbadname.s2lib.pathing.movement.type;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.pathing.movement.Moves;
import com.genericbadname.s2lib.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class ParkourMovement extends WalkMovement {
    @Override
    public void move(Mob mob, BlockPos pos) {
        float yRot = (float) Moves.rotFromPos(mob.blockPosition(), pos);
        Vec3 vec = MathUtil.calculateLaunchVec(mob.position(), Vec3.atCenterOf(pos), 20, 0.08, 0.09);

        mob.setDeltaMovement(vec);

        //mob.setYRot(yRot);
        //mob.setYHeadRot(yRot);

        S2Lib.logInfo("{}", vec);
    }
}
