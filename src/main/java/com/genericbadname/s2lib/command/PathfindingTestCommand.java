package com.genericbadname.s2lib.command;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.pathing.AStarPathCalculator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public class PathfindingTestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
        dispatcher.register(((Commands.literal("pathtest")
                .requires((source) -> source.hasPermission(2)))
                    .then(Commands.argument("start", BlockPosArgument.blockPos())
                            .then(Commands.argument("end", BlockPosArgument.blockPos())
                                    .executes((ctx) -> run(ctx.getSource(), BlockPosArgument.getLoadedBlockPos(ctx, "start"), BlockPosArgument.getLoadedBlockPos(ctx, "end")))))));
    }

    public static int run(CommandSourceStack ctx, BlockPos start, BlockPos end) {
        S2Lib.LOGGER.info("running pathfinder");

        AStarPathCalculator pathfinder = new AStarPathCalculator(start, end, ctx.getLevel());

        // loop through path
        for (BlockPos pos : pathfinder.calculate().getPositions()) {
            ctx.getLevel().setBlock(pos, Blocks.RED_CONCRETE.defaultBlockState(), 3);
        }

        S2Lib.LOGGER.info("done :)");

        return Command.SINGLE_SUCCESS;
    }
}
