package com.genericbadname.s2lib.command;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.pathing.AStarPathCalculator;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Node;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

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
        // loop through path
        AStarPathCalculator calculator = new AStarPathCalculator();

        List<S2Node> nodes = calculator.calculate(BetterBlockPos.from(start), BetterBlockPos.from(end), ctx.getLevel()).getPositions();
        S2Lib.LOGGER.info("Got node list of size {}", nodes.size());

        for (S2Node node : nodes) {
            ctx.getLevel().setBlock(node.getPos(), Blocks.RED_CONCRETE.defaultBlockState(), 3);
        }

        S2Lib.LOGGER.info("done :)");

        return Command.SINGLE_SUCCESS;
    }
}
