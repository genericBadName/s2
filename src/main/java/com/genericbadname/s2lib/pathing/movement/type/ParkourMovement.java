package com.genericbadname.s2lib.pathing.movement.type;

import com.genericbadname.s2lib.config.ServerConfig;
import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

public class ParkourMovement implements IMovement {
    private final int jumpDistance;

    public ParkourMovement(int jumpDistance) {
        this.jumpDistance = jumpDistance;
    }
    @Override
    public void move(Mob mob, BlockPos pos) {
        float yRot = (float) Moves.rotFromPos(mob.blockPosition(), pos);

        mob.lerpTo(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, yRot, 0, 3, false);
        mob.setYHeadRot(yRot);
    }

    @Override
    public double cost(Mob mob, BlockPos start, BlockPos end) {
        return start.distSqr(end) * jumpDistance * ServerConfig.JUMP_COST_MULTIPLIER.get();
    }
}
