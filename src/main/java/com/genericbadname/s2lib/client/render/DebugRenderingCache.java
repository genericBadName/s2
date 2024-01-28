package com.genericbadname.s2lib.client.render;

import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Path;
import com.genericbadname.s2lib.pathing.movement.PositionValidity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class DebugRenderingCache {
    private static final Object2ObjectOpenHashMap<UUID, S2Path> activePaths = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<BetterBlockPos, Boolean> calculatedBlocks = new Object2ObjectOpenHashMap<>();

    public static void putPath(UUID uuid, S2Path path) {
        activePaths.put(uuid, path);
    }
    public static void putBlock(BetterBlockPos pos, boolean valid) {
        if (pos == null) return;

        calculatedBlocks.put(pos, valid);
    }

    public static void removePath(UUID uuid) {
        activePaths.remove(uuid);
    }
    public static void removeBlock(BetterBlockPos pos) {
        calculatedBlocks.remove(pos);
    }

    public static void clearPaths() {activePaths.clear();}
    public static void clearBlocks() {
        calculatedBlocks.clear();
    }

    public static ObjectCollection<S2Path> getPaths() {
        return activePaths.values();
    }

    public static ObjectSet<Map.Entry<BetterBlockPos, Boolean>> getBlocks() {
        return (calculatedBlocks.size() > 0) ? calculatedBlocks.entrySet() : null;
    }
}
