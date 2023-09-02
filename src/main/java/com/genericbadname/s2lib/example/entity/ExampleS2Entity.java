package com.genericbadname.s2lib.example.entity;

import com.genericbadname.s2lib.example.goal.GetToBlockGoal;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class ExampleS2Entity extends Zombie {
    public ExampleS2Entity(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new GetToBlockGoal(this, new BetterBlockPos(0, -60, 0)));
    }
}
