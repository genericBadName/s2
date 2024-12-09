package com.genericbadname.s2lib.example.entity;

import com.genericbadname.s2lib.S2Lib;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ExampleS2Renderer extends EntityRenderer<ExampleS2Entity> {
    public ExampleS2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(ExampleS2Entity entity) {
        return S2Lib.asResource("textures/entity/example/example.png");
    }
}
