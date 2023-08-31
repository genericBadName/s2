package com.genericbadname.s2lib.pathing;

import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.core.BlockPos;

/**
 * A singular node in an {@link S2Path}. Stores cost-related information and its parent node, for later traversal.
 */
public class S2Node {
    private int gCost;
    private int hCost;
    private final BlockPos pos;
    private final Moves move;
    private S2Node parent;

    public S2Node(BlockPos pos, Moves move) {
        this.pos = pos;
        this.move = move;
    }

    public S2Node(BlockPos pos, Moves move, S2Node parent) {
        this.pos = pos;
        this.parent = parent;
        this.move = move;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getGCost() {
        return gCost;
    }

    public int getHCost() {
        return hCost;
    }

    public int getFCost() {
        return gCost + hCost;
    }

    public S2Node getParent() {
        return parent;
    }

    public Moves getMove() {
        return move;
    }

    public void setParent(S2Node parent) {
        this.parent = parent;
    }

    public void setGCost(int gCost) {
        this.gCost = gCost;
    }

    public void setHCost(int hCost) {
        this.hCost = hCost;
    }
}
