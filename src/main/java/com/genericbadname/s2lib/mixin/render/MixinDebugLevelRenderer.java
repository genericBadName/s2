package com.genericbadname.s2lib.mixin.render;

import com.genericbadname.s2lib.client.render.IRenderer;
import com.genericbadname.s2lib.client.render.PathRenderer;
import com.genericbadname.s2lib.client.render.DebugRenderingCache;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Path;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Map;

@Mixin(LevelRenderer.class)
public class MixinDebugLevelRenderer {
    @Inject(method = "renderLevel", at = @At("TAIL"))
    public void renderLevel(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        IRenderer.startLines(Color.BLACK, 5, true);

        // render paths
        for (S2Path path : DebugRenderingCache.getPaths()) {
            PathRenderer.renderPath(poseStack, path, 0.5F);
        }

        // render calculated blocks
        for (Map.Entry<BetterBlockPos, Boolean> block : DebugRenderingCache.getBlocks()) {
            boolean valid = block.getValue();
            Color color = valid ? Color.GREEN : Color.RED;

            IRenderer.glColor(color, 0.4F);
            IRenderer.emitAABB(poseStack, AABB.of(new BoundingBox(block.getKey())));
        }

        IRenderer.endLines(true);
    }
}
