package com.genericbadname.s2lib.pathing;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.bakery.eval.BakedLevelAccessor;
import com.genericbadname.s2lib.config.ServerConfig;
import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import com.genericbadname.s2lib.pathing.movement.PositionValidity;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.compress.utils.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.genericbadname.s2lib.bakery.eval.BakedLevelAccessor.HazardLevel;

public class AStarPathCalculator {
    private static final double minimumImprovement = 0.01;
    private static final long timeoutTime = 2 * 1000; // in ms

    private BinaryHeapOpenSet openSet;
    private Long2ObjectOpenHashMap<S2Node> map;

    private final BakedLevelAccessor bakery;

    public AStarPathCalculator(Level level) {
        this.bakery = new BakedLevelAccessor(level);
        this.openSet = new BinaryHeapOpenSet();
        this.map = new Long2ObjectOpenHashMap<>();
    }

    // run until completion
    public Optional<S2Path> calculate(BetterBlockPos startPos, BetterBlockPos endPos) {
        reset();

        S2Lib.logInfo("Starting pathfinder from {} to {} in {}", startPos, endPos, bakery);
        if (startPos == null || endPos == null) return Optional.empty();

        long startTime = System.currentTimeMillis();
        int numNodes = 1;

        // create and add start node
        S2Node start = new S2Node(startPos, Moves.START, 0, startPos.distSqr(endPos));

        openSet.insert(start);
        map.put(BetterBlockPos.longHash(startPos), start);

        // start looping through nodes
        while (!openSet.isEmpty() && System.currentTimeMillis() - startTime <= timeoutTime) {
            S2Node current = openSet.removeLowest(); // get lowest f-score

            // if at goal, stop searching and retrace path
            if (current.getPos().equals(endPos)) {
                logOut(true, numNodes, startTime);

                return retrace(current);
            }

            // go through each moveset to find the next best move type
            for (Moves move : availableMoves()) {
                IMovement movement = move.type;
                // iterate over each step of the movement (used in multi-block checks like parkour jumps)
                for (int step=0;step<move.steps;step++) {
                    BetterBlockPos neighborPos = current.getPos().offset(move.offset);
                    //S2Lib.logInfo("current pos: {}", neighborPos);

                    // check if neighbor is valid, otherwise skip node
                    // TODO: possibly cache this result in the bakery for faster lookup?
                    PositionValidity validity = movement.isValidPosition(bakery, neighborPos);
                    if (validity.equals(move.stopCondition) || (move.stopCondition.equals(PositionValidity.NONE) && validity.ordinal() > 1)) break;

                    long currentHash = BetterBlockPos.longHash(current.getPos());
                    S2Node neighbor = getNodeAtPosition(neighborPos, BetterBlockPos.longHash(neighborPos), currentHash, move);

                    HazardLevel hazardLevel = bakery.getHazardLevel(neighborPos);
                    double tentativeGCost = (current.getGCost() + move.cost) * hazardLevel.costMultiplier;

                    // this is a better path, go for it!
                    if (neighbor.getGCost() - tentativeGCost > minimumImprovement) {
                        //debugMove(neighborPos, true);
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

        return Optional.empty(); // empty list, meaning no path
    }

    private Optional<S2Path> retrace(S2Node goal) {
        List<S2Node> path = Lists.newArrayList();
        S2Node current = goal;

        // create path by going backwards
        while (current != null) {
            path.add(current);
            current = map.get(current.getParent());
        }

        Collections.reverse(path);

        return Optional.of(new S2Path(path));
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

    // get available moves depending on config
    private Set<Moves> availableMoves() {
        Set<Moves> availableMoves = new ObjectOpenHashSet<>();

        if (ServerConfig.ENABLE_WALKING.get()) {
            availableMoves.addAll(Set.of(Moves.walking()));
        }

        if (ServerConfig.ENABLE_STEP_UP.get()) {
            availableMoves.addAll(Set.of(Moves.stepUp()));
        }

        if (ServerConfig.ENABLE_PARKOUR.get()) {
            availableMoves.addAll(Set.of(Moves.parkour()));
        }

        if (ServerConfig.ENABLE_FALLING.get()) {
            availableMoves.addAll(Set.of(Moves.falling()));
        }

        return availableMoves;
    }

    // debug
    private void logOut(boolean success, int numNodes, long startTime) {
        S2Lib.logInfo(success ? "Found a valid path to target" : "Failed to find a valid path to target");
        S2Lib.logInfo("Open set contains {} nodes", openSet.size());
        S2Lib.logInfo("Considered {} nodes per second", (int) (numNodes * 1.0 / ((System.currentTimeMillis() - startTime) / 1000F)));
    }
}
