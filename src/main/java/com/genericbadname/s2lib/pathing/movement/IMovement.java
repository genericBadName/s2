package com.genericbadname.s2lib.pathing.movement;

import com.genericbadname.s2lib.bakery.eval.BakedLevelAccessor;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Node;
import com.genericbadname.s2lib.pathing.entity.S2Mob;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.List;

public interface IMovement {
    void move(Mob mob, BlockPos pos);

    default double cost(BetterBlockPos start, BetterBlockPos end) {
        return start.distSqr(end);
    }

    boolean isValidPosition(BakedLevelAccessor bakery, BetterBlockPos pos);
}
