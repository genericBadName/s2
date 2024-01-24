package com.genericbadname.s2lib.example.entity;

import com.genericbadname.s2lib.S2Lib;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ExampleS2Model extends GeoModel<ExampleS2Entity> {
    private static final ResourceLocation model = S2Lib.asResource("geo/example.geo.json");
    private static final ResourceLocation texture = S2Lib.asResource("textures/entity/example.png");
    private static final ResourceLocation animation = S2Lib.asResource("animations/example.animation.json");
    @Override
    public ResourceLocation getModelResource(ExampleS2Entity object) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(ExampleS2Entity object) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(ExampleS2Entity animatable) {
        return animation;
    }
}
