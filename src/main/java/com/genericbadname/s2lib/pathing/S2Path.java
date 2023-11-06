package com.genericbadname.s2lib.pathing;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Objects;


public class S2Path {
    private final List<S2Node> positions;

    public S2Path() {
        this.positions = Lists.newArrayList();
    }

    public S2Path(List<S2Node> positions) {
        this.positions = Objects.requireNonNullElseGet(positions, List::of);
    }

    public List<S2Node> getPositions() {
        return positions;
    }

    public boolean isPossible() {
        if (positions == null) return false;
        return !positions.isEmpty();
    }
}
