package com.genericbadname.s2lib.pathing;

import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
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

    public List<S2Node> getNodes() {
        return positions;
    }

    public boolean isPossible() {
        if (positions == null) return false;
        return !positions.isEmpty();
    }

    public void serialize(FriendlyByteBuf buf) {
        serialize(positions, buf);
    }

    public static void serialize(List<S2Node> nodes, FriendlyByteBuf buf) {
        for (S2Node node : nodes) {
            node.serialize(buf);
        }
    }

    public static S2Path deserialize(FriendlyByteBuf buf) {
        S2Node next = S2Node.deserialize(buf);
        List<S2Node> positions = new ArrayList<>();

        while (next != null) {
            positions.add(next);
            next = S2Node.deserialize(buf);
        }

        return new S2Path(positions);
    }

    @Override
    public String toString() {
        return "S2Path{" +
                "positions=" + positions +
                '}';
    }
}
