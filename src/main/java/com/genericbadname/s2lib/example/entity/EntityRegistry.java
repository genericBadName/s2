package com.genericbadname.s2lib.example.entity;

import com.genericbadname.s2lib.S2Lib;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityRegistry {
    public static final EntityType<ExampleS2Entity> EXAMPLE = Registry.register(Registry.ENTITY_TYPE, S2Lib.asResource("example"), FabricEntityTypeBuilder.Living
            .create(MobCategory.MONSTER, ExampleS2Entity::new)
            .dimensions(EntityDimensions.fixed(0.75f, 1.75f))
            .build());
}
