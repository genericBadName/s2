package com.genericbadname.s2lib.pathing.movement.type;

import com.genericbadname.s2lib.pathing.S2Node;
import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class CardinalMovement implements IMovement {
    @Override
    public void move(Mob mob, BlockPos pos) {
        float yRot = (float) Moves.rotFromPos(mob.blockPosition(), pos);

        mob.lerpTo(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, yRot, 0, 10, false);
        mob.setYHeadRot(yRot);
    }

    @Override
    public List<S2Node> getNeighbors(Level level, BlockPos pos, S2Node parent) {
        List<S2Node> directions = List.of(
                new S2Node(pos.north(), Moves.CARDINAL, parent),
                new S2Node(pos.south(), Moves.CARDINAL, parent),
                new S2Node(pos.east(), Moves.CARDINAL, parent),
                new S2Node(pos.west(), Moves.CARDINAL, parent)
        );

        // only return walkable areas
        return directions.stream().filter(node -> !level.getBlockState(node.getPos()).is(Blocks.STONE)).toList();
    }

    @Override
    public int cost(Mob mob, BlockPos start, BlockPos end) {
        return Moves.calculateCost(start, end);
    }
}
