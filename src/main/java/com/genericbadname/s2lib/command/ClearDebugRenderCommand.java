package com.genericbadname.s2lib.command;

import com.genericbadname.s2lib.client.render.DebugRenderingCache;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class ClearDebugRenderCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("cleardebugrender").executes(ctx -> {
                    DebugRenderingCache.clearBlocks();
                    DebugRenderingCache.clearPaths();
                    ctx.getSource().sendFeedback(Component.literal("Cleared client render debug cache!"));

                    return 0;
                })
        );
    }
}
