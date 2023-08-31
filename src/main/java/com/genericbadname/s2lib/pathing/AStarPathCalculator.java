package com.genericbadname.s2lib.pathing;

import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AStarPathCalculator {
    private final BlockPos startPos;
    private final BlockPos endPos;

    private Level level;

    public AStarPathCalculator(BlockPos startPos, BlockPos endPos, Level level) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.level = level;
    }

    // run until completion
    public S2Path calculate() {
        List<S2Node> openSet = Lists.newArrayList();
        Set<S2Node> closedSet = Sets.newHashSet();

        openSet.add(new S2Node(startPos, Moves.CARDINAL));

        while (!openSet.isEmpty()) {
            // find lowest F cost in open set
            S2Node currentNode = null;

            for (S2Node atIndex : openSet) {
                if (currentNode == null) {
                    currentNode = atIndex;
                    continue;
                }

                if (atIndex.getFCost() < currentNode.getFCost()) {
                    currentNode = atIndex;
                }
            }

            // remove and add
            openSet.remove(currentNode);
            closedSet.add(currentNode);

            // found goal
            if (currentNode.getPos().equals(endPos)) {
                return retrace(currentNode);
            }

            // time to find the neighbors!
            // calculate neighbors using each of the moves
            for (Moves move : Moves.values()) {
                List<S2Node> neighbors = move.get().getNeighbors(level, currentNode.getPos(), currentNode);
                for (S2Node neighbor : neighbors) {
                    // see if child's position is in closed set
                    if (closedSet.contains(neighbor)) continue;

                    neighbor.setGCost(currentNode.getGCost() + Moves.calculateCost(neighbor.getPos(), currentNode.getPos()));
                    neighbor.setHCost(Moves.calculateCost(neighbor.getPos(), endPos));

                    // see if child's position is in open set
                    if (isInSet(neighbor, openSet)) continue;

                    openSet.add(neighbor);
                }
            }
        }

        return new S2Path(Lists.newArrayList()); // empty list, meaning no path
    }

    private S2Path retrace(S2Node goal) {
        List<S2Node> path = Lists.newArrayList();
        S2Node current = goal;

        // create path by going backwards
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }

        Collections.reverse(path);

        return new S2Path(path);
    }

    private boolean isInSet(S2Node child, Collection<S2Node> set) {
        for (S2Node setNode : set) {
            if (setNode.getPos().equals(child.getPos())) return true;
        }

        return false;
    }
}
