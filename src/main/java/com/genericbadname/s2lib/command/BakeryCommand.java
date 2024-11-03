package com.genericbadname.s2lib.command;

import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.commands.Commands.literal;

public class BakeryCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(

                literal("bakery")
                        .requires(src -> src.hasPermission(2))
                        .then(literal("reload")
                                .executes(ctx -> {
                                    CommandSourceStack src = ctx.getSource();
                                    src.getServer().reloadBakeries();
                                    src.sendSystemMessage(Component.literal("Reloaded all bakeries"));
                                    return 1;
                                })
                        )
                        .then(literal("write")
                                .then(literal("all")
                                        .executes(ctx -> {
                                            CommandSourceStack src = ctx.getSource();
                                            src.getServer().writeBakeries();
                                            src.sendSystemMessage(Component.literal("Wrote all Bakeries to disk"));
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("scan")
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
        );
    }
}
