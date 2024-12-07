package com.genericbadname.s2lib.command;

import com.genericbadname.s2lib.bakery.storage.Loaf;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import static net.minecraft.commands.Commands.literal;

public class BakeryCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("bakery")
                        .requires(src -> src.hasPermission(2))
                        .then(literal("write_all")
                                .executes(ctx -> {
                                    CommandSourceStack src = ctx.getSource();
                                    src.getServer().writeBakeries();
                                    src.sendSystemMessage(Component.literal("Wrote all Bakeries to disk"));
                                    return 1;
                                })
                        )
                        .then(literal("scan_chunk")
                                .executes(ctx -> {
                                    CommandSourceStack src = ctx.getSource();
                                    if (!src.isPlayer()) return 1;
                                    ServerPlayer player = src.getPlayer();

                                    ChunkPos posC = player.chunkPosition();

                                    src.getServer().getBakery(src.getLevel().dimension()).scanChunk(posC);
                                    src.sendSystemMessage(Component.literal("Scanned chunk (" + posC.x + ", " + posC.z + ")"));
                                    return 1;
                                })
                        )
                        .then(literal("load_chunk")
                                .executes(ctx -> {
                                    CommandSourceStack src = ctx.getSource();
                                    if (!src.isPlayer()) return 1;
                                    ServerPlayer player = src.getPlayer();

                                    ChunkPos posC = player.chunkPosition();

                                    Loaf loaf = src.getServer().getBakery(src.getLevel().dimension()).attemptRead(posC);

                                    if (loaf == null) {
                                        src.sendSystemMessage(Component.literal("Failed to load chunk (" + posC.x + ", " + posC.z + ") from disk"));
                                    } else {
                                        src.sendSystemMessage(Component.literal("Loaded chunk (" + posC.x + ", " + posC.z + ") from disk"));
                                    }
                                    return 1;
                                })
                        )
                        .then(literal("clear_all")
                                .executes(ctx -> {
                                    CommandSourceStack src = ctx.getSource();
                                    src.getServer().clearBakeries();
                                    src.sendSystemMessage(Component.literal("Cleared all Bakeries in memory"));
                                    return 1;
                                })
                        )
        );
    }
}
