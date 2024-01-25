package com.genericbadname.s2lib.mixin.render;

import com.genericbadname.s2lib.client.render.PathRenderer;
import com.genericbadname.s2lib.client.render.PathRenderingCache;
import com.genericbadname.s2lib.pathing.S2Path;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Inject(method = "renderLevel", at = @At("TAIL"))
    public void renderLevel(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        for (S2Path path : PathRenderingCache.getPaths()) {
            PathRenderer.renderPath(poseStack, path, 0.5F);
        }
    }
}
