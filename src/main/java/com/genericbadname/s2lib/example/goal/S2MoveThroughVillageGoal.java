package com.genericbadname.s2lib.example.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BooleanSupplier;

public class S2MoveThroughVillageGoal extends Goal {

    public S2MoveThroughVillageGoal(PathfinderMob mob, double speedModifier, boolean onlyAtNight, int distanceToPoi, BooleanSupplier canDealWithDoors) {

    }

    @Override
    public boolean canUse() {
        return false;
    }
}
