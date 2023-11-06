package com.genericbadname.s2lib.example.entity;

import com.genericbadname.s2lib.example.goal.GetToBlockGoal;
import com.genericbadname.s2lib.example.goal.S2NearestAttackableTargetGoal;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.entity.S2Mob;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class ExampleS2Entity extends S2Mob implements IAnimatable {
    private static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public ExampleS2Entity(EntityType<? extends S2Mob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 50.0).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.ARMOR, 2.0);
    }

    @Override
    protected void registerGoals() {
        //goalSelector.addGoal(0, new GetToBlockGoal(this, new BetterBlockPos(10, -61, 5)));
        targetSelector.addGoal(1, new S2NearestAttackableTargetGoal<>(this, Pig.class, 10, false, false, null));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "Walking", 5, this::walkAnimController));
    }

    protected <E extends ExampleS2Entity> PlayState walkAnimController(final AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(WALK_ANIM);

            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
