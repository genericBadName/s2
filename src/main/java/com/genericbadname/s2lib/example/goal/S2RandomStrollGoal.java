package com.genericbadname.s2lib.example.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

public class S2RandomStrollGoal extends RandomStrollGoal {
    public S2RandomStrollGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }
}
