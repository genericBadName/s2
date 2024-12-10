package com.genericbadname.s2lib.mixin.entity;

import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Zombie.class)
public class MixinSmartZombie {
    /**
     * @author genericBadName
     * @reason Replace Zombie goals with S2-compatible ones
     */
    @Overwrite
    protected void registerGoals() {

    }
}
