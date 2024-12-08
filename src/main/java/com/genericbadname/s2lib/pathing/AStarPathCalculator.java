package com.genericbadname.s2lib.pathing;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.bakery.HazardLevel;
import com.genericbadname.s2lib.bakery.storage.Bakery;
import com.genericbadname.s2lib.config.CommonConfig;
import com.genericbadname.s2lib.config.ServerConfig;
import com.genericbadname.s2lib.network.S2NetworkingConstants;
import com.genericbadname.s2lib.network.S2NetworkingUtil;
import com.genericbadname.s2lib.network.packet.RenderNodeUpdateS2CPacket;
import com.genericbadname.s2lib.pathing.movement.Moves;
import com.genericbadname.s2lib.pathing.movement.PositionValidity;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.compress.utils.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AStarPathCalculator {
    private static final double minimumImprovement = 0.01;
    private static final ImmutableSet<Moves> availableMoves;

    private BinaryHeapOpenSet openSet;
    private Long2ObjectMap<S2Node> map;
    private long startTime;

    public AStarPathCalculator() {
        this.openSet = new BinaryHeapOpenSet();
        this.map = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
    }

    // set available moves based on config
    static {
        Set<Moves> movesToSet = new ObjectOpenHashSet<>();

        if (ServerConfig.ENABLE_WALKING.get()) {
            movesToSet.addAll(Set.of(Moves.walking()));
        }

        if (ServerConfig.ENABLE_STEP_UP.get()) {
            movesToSet.addAll(Set.of(Moves.stepUp()));
        }

        if (ServerConfig.ENABLE_PARKOUR.get()) {
            // removed parkour. this has caused me so much headache.
            //movesToSet.addAll(Set.of(Moves.parkour()));
        }

        if (ServerConfig.ENABLE_FALLING.get()) {
            movesToSet.addAll(Set.of(Moves.falling()));
        }

        availableMoves = ImmutableSet.copyOf(movesToSet);
    }

    // run until completion
    public S2Path calculate(BetterBlockPos startPos, BetterBlockPos endPos, Bakery bakery) {
        reset();

        S2Lib.logInfo("Starting pathfinder from {} to {} in {}", startPos, endPos, bakery);
        if (startPos == null || endPos == null) return null;

        startTime = System.currentTimeMillis();
        int numNodes = 1;

        // create and add start node
        S2Node start = new S2Node(startPos, Moves.START, 0, startPos.distSqr(endPos));

        openSet.insert(start);
        map.put(BetterBlockPos.longHash(startPos), start);

        // start looping through nodes
        while (!openSet.isEmpty() && shouldContinueRunning()) {
            S2Node current = openSet.removeLowest(); // get lowest f-score

            // if at goal, stop searching and retrace path
            if (current.getPos().equals(endPos)) {
                logOut(true, numNodes, startTime);

                return retrace(current);
            }

            // go through each moveset to find the next best move type
            for (Moves move : availableMoves) {
                // iterate over each step of the movement (used in multi-block checks like parkour jumps)
                for (int step = 0; step < move.steps; step++) {
                    BetterBlockPos neighborPos = current.getPos().offset(move.stepVec.multiply(step)).offset(move.offset);

                    // check if neighbor is valid, otherwise skip node
                    // TODO: possibly cache this result in the bakery for faster lookup?
                    PositionValidity validity = move.positionValidator.apply(bakery, neighborPos);
                    if (CommonConfig.DEBUG_PATH_CALCULATIONS.get()) S2NetworkingUtil.dispatchAll(S2NetworkingConstants.RENDER_NODE_UPDATE, RenderNodeUpdateS2CPacket.create(neighborPos, validity == PositionValidity.SUCCESS), bakery.getServer());

                    if (validity != PositionValidity.SUCCESS) {
                        if (validity == move.stopCondition) break;

                        continue;
                    }

                    long currentHash = BetterBlockPos.longHash(current.getPos());
                    S2Node neighbor = getNodeAtPosition(neighborPos, BetterBlockPos.longHash(neighborPos), currentHash, move);

                    HazardLevel hazardLevel = bakery.getHazardLevel(neighborPos);
                    double tentativeGCost = (current.getGCost() + move.cost) * hazardLevel.getCostMultiplier();

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

                        // for debug :)
                        if (CommonConfig.DEBUG_PATH_CALCULATIONS.get()) S2NetworkingUtil.dispatchAll(S2NetworkingConstants.RENDER_NODE_UPDATE, RenderNodeUpdateS2CPacket.create(neighborPos, true), bakery.getServer());
                    }
                }
            }
        }

        logOut(false, numNodes, startTime);

        return null; // empty list, meaning no path
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

    // clear data from previous pathfinding session
    private void reset() {
        openSet = new BinaryHeapOpenSet();
        map = new Long2ObjectOpenHashMap<>();
    }

    // debug
    private void logOut(boolean success, int numNodes, long startTime) {
        S2Lib.logInfo(success ? "Found a valid path to target" : "Failed to find a valid path to target");
        S2Lib.logInfo("Open set contains {} nodes", openSet.size());
        S2Lib.logInfo("Considered {} nodes per second", (int) (numNodes * 1.0 / ((System.currentTimeMillis() - startTime) / 1000F)));
    }

    private boolean shouldContinueRunning() {
        if (ServerConfig.TIMEOUT_TIME.get() == -1) return true;
        return System.currentTimeMillis() - startTime <= ServerConfig.TIMEOUT_TIME.get();
    }
}
