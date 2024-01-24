package com.genericbadname.s2lib.example.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ExampleS2Renderer extends GeoEntityRenderer<ExampleS2Entity> {
    public ExampleS2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ExampleS2Model());
    }
}
