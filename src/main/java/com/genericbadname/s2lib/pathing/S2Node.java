package com.genericbadname.s2lib.pathing;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.pathing.movement.ActionCosts;
import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.core.BlockPos;

/**
 * A singular node in an {@link S2Path}. Stores cost-related information and its parent node, for later traversal.
 */
public class S2Node {
    public double gCost = ActionCosts.COST_INF.cost;
    private double hCost;

    private final BetterBlockPos pos;
    private final Moves move;

    private S2Node parent;
    private int heapPosition;

    public S2Node(BetterBlockPos pos, Moves move) {
        this.pos = pos;
        this.move = move;
        this.heapPosition = -1;
    }

    public S2Node(BetterBlockPos pos, Moves move, S2Node parent) {
        this.pos = pos;
        this.move = move;
        this.heapPosition = -1;
        this.parent = parent;
    }

    public S2Node(BetterBlockPos pos, Moves move, double gCost, double hCost) {
        this.pos = pos;
        this.move = move;
        this.heapPosition = -1;
        this.gCost = gCost;
        this.hCost = hCost;
    }

    // boilerplate galore
    public BetterBlockPos getPos() {
        return pos;
    }

    public double getGCost() {
        return gCost;
    }

    public double getHCost() {
        return hCost;
    }

    public double getFCost() {
        return gCost + hCost;
    }

    public S2Node getParent() {
        return parent;
    }

    public Moves getMove() {
        return move;
    }

    public boolean isOpen() {
        return heapPosition != -1;
    }

    public int getHeapPosition() {
        return heapPosition;
    }

    public void setParent(S2Node parent) {
        this.parent = parent;
    }

    public void setGCost(double gCost) {
        this.gCost = gCost;
    }

    public void setHCost(double hCost) {
        this.hCost = hCost;
    }

    public void setHeapPosition(int heapPosition) {
        this.heapPosition = heapPosition;
    }

    @Override
    public String toString() {
        return "S2Node{" +
                "gCost=" + gCost +
                ", hCost=" + hCost +
                ", pos=" + pos +
                ", heapPosition=" + heapPosition +
                '}';
    }

    @Override
    public int hashCode() {
        return (int) BetterBlockPos.longHash(pos.x, pos.y, pos.z);
    }

    @Override
    public boolean equals(Object obj) {
        // GOTTA GO FAST
        // ALL THESE CHECKS ARE FOR PEOPLE WHO WANT SLOW CODE
        // SKRT SKRT
        //if (obj == null || !(obj instanceof PathNode)) {
        //    return false;
        //}

        final S2Node other = (S2Node) obj;
        //return Objects.equals(this.pos, other.pos) && Objects.equals(this.goal, other.goal);

        return pos.equals(other.pos);
    }
}
