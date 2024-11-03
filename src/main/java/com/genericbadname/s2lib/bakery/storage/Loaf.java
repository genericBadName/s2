package com.genericbadname.s2lib.bakery.storage;

import com.genericbadname.s2lib.bakery.HazardLevel;
import net.minecraft.world.level.ChunkPos;

// y x z format
public class Loaf {
    private final HazardLevel[][][] chunkHazard;

    public Loaf(HazardLevel[][][] chunkHazard, ChunkPos chunkPos) {
        if (chunkHazard == null || chunkPos == null) throw new IllegalArgumentException("chunkHazard and chunkPos cannot be null!");

        this.chunkHazard = chunkHazard;
    }

    public HazardLevel[][][] chunkHazard() {
        return chunkHazard;
    }
}
