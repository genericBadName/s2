package com.genericbadname.s2lib.pathing;

import net.minecraft.core.BlockPos;

/**
 * A singular node in an {@link S2Path}. Stores cost-related information and its parent node, for later traversal.
 */
public class S2Node {
    private int gCost;
    private int hCost;
    private final BlockPos pos;
    private S2Node parent;

    public S2Node(BlockPos pos, int gCost, int hCost) {
        this.pos = pos;
        this.gCost = gCost;
        this.hCost = hCost;
    }

    public S2Node(BlockPos pos) {
        this.pos = pos;
    }

    public S2Node(BlockPos pos, S2Node parent) {
        this.pos = pos;
        this.parent = parent;
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
