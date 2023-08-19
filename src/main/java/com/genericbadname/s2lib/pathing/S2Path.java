package com.genericbadname.s2lib.pathing;

import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Objects;


public class S2Path {
    private final List<BlockPos> positions;

    public S2Path(List<BlockPos> positions) {
        this.positions = Objects.requireNonNullElseGet(positions, List::of);
    }

    public List<BlockPos> getPositions() {
        return positions;
    }

    public boolean isPossible() {
        if (positions == null) return false;
        return positions.size() > 0;
    }
}
