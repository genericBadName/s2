package com.genericbadname.s2lib.mixin.chunk;

import com.genericbadname.s2lib.bakery.storage.Bakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "onBlockStateChange", at = @At("HEAD"))
    public void onBlockStateChange(BlockPos pos, BlockState blockState, BlockState newState, CallbackInfo ci) {
        ServerLevel thiz = (ServerLevel)(Object)this;
        Bakery bakery = thiz.getServer().getBakery(thiz.dimension());

        if (bakery != null) {
            //bakery.updateHazardLevel(BetterBlockPos.from(pos), newState);
        }
    }
}
