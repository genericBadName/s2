package com.genericbadname.s2lib.bakery;

import com.genericbadname.s2lib.config.ServerConfig;

import java.nio.ByteBuffer;
import java.util.Arrays;

public enum HazardLevel {
    UNKNOWN(ServerConfig.UNKNOWN_COST_MULTIPLIER.get()),
    PASSABLE(ServerConfig.PASSABLE_COST_MULTIPLIER.get()),
    WALKABLE(ServerConfig.WALKABLE_COST_MULTIPLIER.get()),
    POTENTIALLY_AVOID(ServerConfig.POTENTIALLY_AVOID_COST_MULTIPLIER.get()),
    AVOID(ServerConfig.AVOID_COST_MULTIPLIER.get()),
    AVOID_AT_ALL_COSTS(ServerConfig.AVOID_COST_MULTIPLIER.get());

    public final double costMultiplier;
    HazardLevel(double costMultiplier) {
        this.costMultiplier = costMultiplier;
    }

    // evil bit hacks
    public static byte[] toBytes8(HazardLevel[] hazards) {
        if (hazards.length != 8) throw new IllegalArgumentException("Hazard array should be 8 in length (24 bits)");

        int buf = 0;

        for (int i=0;i<8;i++) {
            buf = buf | (hazards[i].ordinal() << ((7-i)*3));
        }

        return Arrays.copyOfRange(ByteBuffer.allocate(4).putInt(buf).array(), 1, 4);
    }

    // 3 bits per hazard
    public static byte[] toBytes16(HazardLevel[] hazards) {
        if (hazards.length != 16) throw new IllegalArgumentException("Hazard array should be 16 in length (48 bits)");

        long buf = 0;

        for (int i=0;i<16;i++) {
            buf = buf | ((long) hazards[i].ordinal() << ((15-i)*3));
        }

        return Arrays.copyOfRange(ByteBuffer.allocate(8).putLong(buf).array(), 2, 8);
    }
}