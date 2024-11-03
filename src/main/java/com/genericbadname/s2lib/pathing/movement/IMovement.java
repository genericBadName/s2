package com.genericbadname.s2lib.pathing.movement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

public interface IMovement {
    void move(Mob mob, BlockPos pos);
}
