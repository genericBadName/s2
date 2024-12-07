package com.genericbadname.s2lib.bakery;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.config.ServerConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.ServiceConfigurationError;

public enum HazardLevel {
    UNKNOWN(1.0, ServerConfig.UNKNOWN_COST_MULTIPLIER),
    PASSABLE(1.0, ServerConfig.PASSABLE_COST_MULTIPLIER),
    WALKABLE(1.0, ServerConfig.WALKABLE_COST_MULTIPLIER),
    POTENTIALLY_AVOID(1.0, ServerConfig.POTENTIALLY_AVOID_COST_MULTIPLIER),
    AVOID(1.0, ServerConfig.AVOID_COST_MULTIPLIER),
    AVOID_AT_ALL_COSTS(1.0, ServerConfig.AVOID_COST_MULTIPLIER);

    public static final long bufMask = 7L;

    private double costMultiplier;
    HazardLevel(double defaultValue, ForgeConfigSpec.ConfigValue<Double> configValue) {
        try {
            this.costMultiplier = configValue.get();
        } catch (ServiceConfigurationError | NoClassDefFoundError e) {
            this.costMultiplier = defaultValue;
        }
    }

    public double getCostMultiplier() {
        return costMultiplier;
    }

    // evil bit hacks, 3 bits per hazard
    public static byte[] toBytes16(HazardLevel[] hazards) {
        if (hazards.length != 16) throw new IllegalArgumentException("Hazard array should be 16 in length (48 bits)");

        long buf = 0;

        for (int i=0;i<16;i++) {
            buf = buf | ((long) hazards[i].ordinal() << ((15-i)*3));
        }

        return Arrays.copyOfRange(ByteBuffer.allocate(8).putLong(buf).array(), 2, 8);
    }

    public static HazardLevel[] fromBytes16(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
        HazardLevel[] hazards = new HazardLevel[16];

        bb.put(2, bytes);
        bb.put(0, new byte[]{0, 0}); // fill last 2 bytes

        long buf = bb.getLong();

        // now get hazards
        for (int z=0;z<16;z++) {
            int bufSlice = (int) (buf & bufMask);
            try {
                // 7 mask might be backwards
                hazards[z] = HazardLevel.values()[bufSlice];
            } catch(IndexOutOfBoundsException e) {
                S2Lib.LOGGER.error("Failed to read bytes: IndexOutOfBounds. Buffer may be corrupted!");
                return null;
            }

            buf = buf >> 3;
        }

        return reverse(hazards);
    }

    private static HazardLevel[] reverse(HazardLevel[] arr) {
        HazardLevel[] temp = new HazardLevel[arr.length];

        for (int i=0;i<arr.length;i++) {
            temp[arr.length-i-1] = arr[i];
        }

        return temp;
    }
}