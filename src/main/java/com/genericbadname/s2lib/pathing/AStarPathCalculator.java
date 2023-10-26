package com.genericbadname.s2lib.pathing;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.data.tag.ModBlockTags;
import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.compress.utils.Lists;

import java.util.Collections;
import java.util.List;

public class AStarPathCalculator {
    private static final double minimumImprovement = 0.01;
    private static final long timeoutTime = 5 * 1000; // in ms

    private final BinaryHeapOpenSet openSet;
    private final Long2ObjectOpenHashMap<S2Node> map;

    private final Level level;

    public AStarPathCalculator(Level level) {
        this.level = level;
        this.openSet = new BinaryHeapOpenSet();
        this.map = new Long2ObjectOpenHashMap<>();
    }

    // run until completion
    public S2Path calculate(BetterBlockPos startPos, BetterBlockPos endPos) {
        S2Lib.logInfo("Starting pathfinder from {} to {} in {}", startPos, endPos, level);
        long startTime = System.currentTimeMillis();
        int numNodes = 1;

        // create and add start node
        S2Node start = new S2Node(startPos, Moves.START, 0, startPos.distSqr(endPos));

        openSet.insert(start);
        map.put(BetterBlockPos.longHash(startPos), start);

        // start looping through nodes
        while (!openSet.isEmpty() && System.currentTimeMillis() - startTime <= timeoutTime) {
            S2Node current = openSet.removeLowest(); // get lowest f-score
            S2Lib.logInfo("Current node: {}", current);

            // if at goal, stop searching and retrace path
            if (current.getPos().equals(endPos)) {
                logOut(true, numNodes, startTime);

                return retrace(current);
            }

            // go through each moveset to find the next best move type
            for (Moves move : Moves.values()) {
                IMovement movement = move.type;
                // iterate over each step of the movement (used in multi-block checks like parkour jumps)
                for (int step=0;step<move.steps;step++) {
                    BetterBlockPos neighborPos = current.getPos().offset(move.offset.offset(move.stepVec.multiply(step)));

                    // check if neighbor is valid, otherwise skip node
                    // DEBUG
                    //debugMove(neighborPos);
                    if (!movement.isValidPosition(level, neighborPos)) {
                        if (move.fastFail) {
                            break;
                        } else {
                            continue;
                        }
                    }

                    long currentHash = BetterBlockPos.longHash(current.getPos());
                    S2Node neighbor = getNodeAtPosition(neighborPos, BetterBlockPos.longHash(neighborPos), currentHash, move);
                    double tentativeGCost = current.getGCost() + move.cost;

                    // this is a better path, go for it!
                    if (neighbor.getGCost() - tentativeGCost > minimumImprovement) {
                        neighbor.setParent(currentHash);
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
        }

        logOut(false, numNodes, startTime);

        return new S2Path(Lists.newArrayList()); // empty list, meaning no path
    }

    private S2Path retrace(S2Node goal) {
        List<S2Node> path = Lists.newArrayList();
        S2Node current = goal;

        // create path by going backwards
        while (current != null) {
            path.add(current);
            current = map.get(current.getParent());
        }

        Collections.reverse(path);

        return new S2Path(path);
    }

    private S2Node getNodeAtPosition(BetterBlockPos pos, long hashCode, long parent, Moves move) {
        S2Node node = map.get(hashCode);
        if (node == null) {
            node = new S2Node(pos, move, parent);
            map.put(hashCode, node);
        }

        return node;
    }

    // debug
    private void logOut(boolean success, int numNodes, long startTime) {
        S2Lib.logInfo(success ? "Found a valid path to target" : "Failed to find a valid path to target");
        S2Lib.logInfo("Open set contains {} nodes", openSet.size());
        S2Lib.logInfo("Considered {} nodes per second", (int) (numNodes * 1.0 / ((System.currentTimeMillis() - startTime) / 1000F)));
    }

    private void debugMove(BetterBlockPos neighborPos) {
        if (level.getBlockState(neighborPos).is(ModBlockTags.PASSABLE)) {
            level.setBlock(neighborPos, Blocks.RED_STAINED_GLASS.defaultBlockState(), 3);
        }
    }
}
