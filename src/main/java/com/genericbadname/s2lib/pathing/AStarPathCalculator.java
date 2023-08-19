package com.genericbadname.s2lib.pathing;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AStarPathCalculator {
    private BlockPos startPos;
    private BlockPos endPos;

    private Level level;

    public AStarPathCalculator(BlockPos startPos, BlockPos endPos, Level level) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.level = level;
    }

    // add all neighbor nodes around a given position
    public List<S2Node> getNeighbors(BlockPos pos, S2Node parent) {
        List<S2Node> directions = List.of(
                new S2Node(pos.north(), parent),
                new S2Node(pos.south(), parent),
                new S2Node(pos.east(), parent),
                new S2Node(pos.west(), parent)
        );

        // only return walkable areas
        return directions.stream().filter(node -> !level.getBlockState(node.getPos()).is(Blocks.STONE)).toList();
    }

    // run until completion
    public S2Path calculate() {
        List<S2Node> openSet = Lists.newArrayList();
        List<S2Node> closedSet = Lists.newArrayList();

        openSet.add(new S2Node(startPos));

        while (!openSet.isEmpty()) {
            // find lowest F cost in open set
            S2Node currentNode = openSet.get(0);
            int currentIndex = 0;

            for (int i = 0; i < openSet.size(); i++) {
                S2Node atIndex = openSet.get(i);
                if (atIndex.getFCost() < currentNode.getFCost()) {
                    currentNode = atIndex;
                    currentIndex = i;
                }
            }

            // remove and add
            openSet.remove(currentIndex);
            closedSet.add(currentNode);

            // found goal
            if (currentNode.getPos().equals(endPos)) {
                List<BlockPos> path = Lists.newArrayList();
                S2Node current = currentNode;

                // create path by going backwards
                while (current != null) {
                    path.add(current.getPos());
                    current = current.getParent();
                }

                Collections.reverse(path);

                return new S2Path(path);
            }

            // time to find the neighbors!
            List<S2Node> neighbors = getNeighbors(currentNode.getPos(), currentNode);
            for (S2Node neighbor : neighbors) {
                // see if child's position is in closed set
                if (isInSet(neighbor, closedSet)) continue;

                neighbor.setGCost(currentNode.getGCost() + calculateCost(neighbor.getPos(), currentNode.getPos()));
                neighbor.setHCost(calculateCost(neighbor.getPos(), endPos));

                // see if child's position is in open set
                if (isInSet(neighbor, openSet)) continue;

                openSet.add(neighbor);
            }
        }

        return new S2Path(Lists.newArrayList()); // empty list, meaning no path
    }

    private int calculateCost(BlockPos self, BlockPos other) {
        return Mth.abs(self.getX() - other.getX()) + Mth.abs(self.getZ() - other.getZ());
    }

    private boolean isInSet(S2Node child, Collection<S2Node> set) {
        for (S2Node setNode : set) {
            if (setNode.getPos().equals(child.getPos())) return true;
        }

        return false;
    }
}
