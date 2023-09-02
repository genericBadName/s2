package com.genericbadname.s2lib.pathing;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.pathing.movement.Moves;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.compress.utils.Lists;

import java.util.Collections;
import java.util.List;

public class AStarPathCalculator {
    private static final double minimumImprovement = 0.01;
    private static final long timeoutTime = 5 * 1000; // in ms

    private final BinaryHeapOpenSet openSet = new BinaryHeapOpenSet();
    private final Long2ObjectOpenHashMap<S2Node> map = new Long2ObjectOpenHashMap<>();

    // run until completion
    public S2Path calculate(BetterBlockPos startPos, BetterBlockPos endPos, Level level) {
        S2Lib.LOGGER.debug("Starting pathfinder from {} to {} in {}", startPos, endPos, level);
        long startTime = System.currentTimeMillis();
        int numNodes = 1;

        // create and add start node
        S2Node start = new S2Node(startPos, Moves.START, 0, startPos.distSqr(endPos));

        openSet.insert(start);
        map.put(BetterBlockPos.longHash(startPos), start);

        // start looping through nodes
        while (!openSet.isEmpty() && System.currentTimeMillis() - startTime <= timeoutTime) {
            S2Node current = openSet.removeLowest(); // get lowest f-score
            S2Lib.LOGGER.info("Current node: {}", current);

            // if at goal, stop searching and retrace path
            if (current.getPos().equals(endPos)) {
                S2Lib.LOGGER.info("Found a valid path to target");
                S2Lib.LOGGER.info("Open set contains {} nodes", openSet.size());
                S2Lib.LOGGER.info("Considered {} nodes per second", (int) (numNodes * 1.0 / ((System.currentTimeMillis() - startTime) / 1000F)));

                return retrace(current);
            }

            // go through each moveset to find the next best move type
            for (Moves move : Moves.values()) {
                BetterBlockPos neighborPos = current.getPos().offset(move.offset);

                // check if neighbor is valid, otherwise skip node
                if (level.getBlockState(neighborPos).is(Blocks.STONE)) continue;
                // DEBUG
                level.setBlock(neighborPos, Blocks.RED_STAINED_GLASS.defaultBlockState(), 3);

                S2Node neighbor = getNodeAtPosition(neighborPos, BetterBlockPos.longHash(neighborPos), current, move);
                double tentativeGCost = current.getGCost() + move.cost;

                // this is a better path, go for it!
                if (neighbor.getGCost() - tentativeGCost > minimumImprovement) {
                    neighbor.setParent(current);
                    neighbor.setGCost(tentativeGCost);
                    neighbor.setHCost(neighbor.getPos().distSqr(endPos));

                    numNodes++;

                    // add to or update set
                    if (neighbor.isOpen()) {
                        openSet.update(neighbor);
                    } else {
                        openSet.insert(neighbor);
                    }
                }
            }
        }

        S2Lib.LOGGER.info("Failed to find a valid path or timed out");
        S2Lib.LOGGER.info("Open set contains {} nodes", openSet.size());
        S2Lib.LOGGER.info("Considered {} nodes per second", (int) (numNodes * 1.0 / ((System.currentTimeMillis() - startTime) / 1000F)));

        return new S2Path(Lists.newArrayList()); // empty list, meaning no path
    }

    private static S2Path retrace(S2Node goal) {
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

    private S2Node getNodeAtPosition(BetterBlockPos pos, long hashCode, S2Node parent, Moves move) {
        S2Node node = map.get(hashCode);
        if (node == null) {
            node = new S2Node(pos, move, parent);
            map.put(hashCode, node);
        }

        return node;
    }
}
