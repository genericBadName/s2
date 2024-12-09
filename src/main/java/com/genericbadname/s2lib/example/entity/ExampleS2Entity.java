package com.genericbadname.s2lib.example.entity;

import com.genericbadname.s2lib.example.goal.S2NearestAttackableTargetGoal;
import com.genericbadname.s2lib.pathing.entity.S2Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class ExampleS2Entity extends S2Mob {

    public ExampleS2Entity(EntityType<? extends S2Mob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 50.0).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.ARMOR, 2.0);
    }

    @Override
    protected void registerGoals() {
        //goalSelector.addGoal(0, new GetToBlockGoal(this, new BetterBlockPos(0, -63, 6)));
        targetSelector.addGoal(1, new S2NearestAttackableTargetGoal<>(this, Pig.class, 10, false, false, null));
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
