package com.genericbadname.s2lib.pathing.movement.type;

import com.genericbadname.s2lib.config.ServerConfig;
import com.genericbadname.s2lib.data.tag.ModBlockTags;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FallMovement extends WalkMovement
{
    private double originalY = 1024;

    public FallMovement() {

    }
    @Override
    public void move(Mob mob, BlockPos pos) {
        if (originalY == 1024) originalY = mob.getY();
        float yRot = (float) Moves.rotFromPos(mob.blockPosition(), pos);

        mob.lerpTo(pos.getX()+0.5, originalY, pos.getZ()+0.5, yRot, 0, 10, false);
        mob.setYHeadRot(yRot);
    }
}
