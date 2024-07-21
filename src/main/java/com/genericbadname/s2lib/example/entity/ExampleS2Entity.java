package com.genericbadname.s2lib.example.entity;

import com.genericbadname.s2lib.example.goal.GetToBlockGoal;
import com.genericbadname.s2lib.example.goal.S2NearestAttackableTargetGoal;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.entity.S2Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ExampleS2Entity extends S2Mob implements GeoEntity {
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
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

    protected <E extends ExampleS2Entity> PlayState walkAnimController(final AnimationState<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(WALK_ANIM);

            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walking", 5, this::walkAnimController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }
}
