package com.genericbadname.s2lib.pathing.movement;

import com.genericbadname.s2lib.pathing.S2Node;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.List;

public interface IMovement {
    void move(Mob mob, BlockPos pos);

    double cost(Mob mob, BlockPos start, BlockPos end);
}
