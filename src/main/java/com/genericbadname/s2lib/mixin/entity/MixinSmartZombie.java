package com.genericbadname.s2lib.mixin.entity;

import com.genericbadname.s2lib.example.goal.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Zombie.class)
public abstract class MixinSmartZombie extends PathfinderMob {
    @Shadow public abstract boolean canBreakDoors();

    protected MixinSmartZombie(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author genericBadName
     * @reason Replace Zombie goals with S2-compatible ones
     */
    @Overwrite
    public void registerGoals() {
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        goalSelector.addGoal(2, new S2AttackGoal());

        goalSelector.addGoal(6, new S2MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
        goalSelector.addGoal(7, new S2RandomStrollGoal(this, 1.0));
        targetSelector.addGoal(1, new S2HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin.class));
        targetSelector.addGoal(2, new S2NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new S2NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        targetSelector.addGoal(3, new S2NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        targetSelector.addGoal(5, new S2NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }
}
