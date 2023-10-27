package com.genericbadname.s2lib.pathing.entity;

import com.genericbadname.s2lib.pathing.AStarPathCalculator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class S2Mob extends PathfinderMob {
    private final AStarPathCalculator calculator;
    public S2Mob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.calculator = new AStarPathCalculator(level);
    }

    public AStarPathCalculator getCalculator() {
        return calculator;
    }
}
