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
import net.minecraft.world.phys.Vec3;
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
        IRenderer.startLines(Color.BLACK, 5, false);

        // render paths
        for (S2Path path : DebugRenderingCache.getPaths()) {
            PathRenderer.renderPath(poseStack, path, 0.5F);
        }

        // render calculated blocks
        if (DebugRenderingCache.getBlocks() == null) {
            IRenderer.endLines(true);

            return;
        }

        for (Map.Entry<BetterBlockPos, Boolean> entry : DebugRenderingCache.getBlocks()) {
            boolean valid = entry.getValue();
            Color color = valid ? Color.GREEN : Color.RED;

            BetterBlockPos pos = entry.getKey();

            IRenderer.glColor(color, 0.4F);
            IRenderer.emitAABB(poseStack, new AABB(new Vec3(pos.x+0.25, pos.y+0.25, pos.z+0.25), new Vec3(pos.x+0.75, pos.y+0.75, pos.z+0.75)));
        }

        IRenderer.endLines(true);
    }
}
