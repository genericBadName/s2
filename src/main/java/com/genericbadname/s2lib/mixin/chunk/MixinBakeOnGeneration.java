package com.genericbadname.s2lib.mixin.chunk;

import com.genericbadname.s2lib.bakery.storage.BakeryAttachment;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public class MixinBakeOnGeneration {
    @Final
    @Shadow
    ServerLevel level;

    // lambda fuckery
    @Inject(method = "method_17227", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;runPostLoad()V", shift = At.Shift.AFTER))
    private void protoChunkToFullChunk(ChunkHolder chunkHolder, ChunkAccess chunkAccess, CallbackInfoReturnable<ChunkAccess> cir, @Local LevelChunk localChunk) {
        if (level instanceof BakeryAttachment) ((BakeryAttachment) level).getBakery(level.dimension()).scanChunk(localChunk);
    }
}
