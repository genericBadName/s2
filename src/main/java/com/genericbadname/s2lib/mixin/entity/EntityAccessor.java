package com.genericbadname.s2lib.mixin.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor
    BlockPos getBlockPosition();

    @Accessor
    UUID getUuid();
}
