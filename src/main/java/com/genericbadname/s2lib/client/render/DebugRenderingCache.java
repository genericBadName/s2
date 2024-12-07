package com.genericbadname.s2lib.client.render;

import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Path;
import com.genericbadname.s2lib.pathing.movement.PositionValidity;
import it.unimi.dsi.fastutil.longs.Long2BooleanArrayMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class DebugRenderingCache {
    private static final Object2ObjectOpenHashMap<UUID, S2Path> activePaths = new Object2ObjectOpenHashMap<>();
    private static final Object2BooleanArrayMap<BetterBlockPos> calculatedBlocks = new Object2BooleanArrayMap<>();

    public static void putPath(UUID uuid, S2Path path) {
        if (path == null) return;

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
        calculatedBlocks.removeBoolean(pos);
    }

    public static void clearPaths() {activePaths.clear();}
    public static void clearBlocks() {
        calculatedBlocks.clear();
    }

    public static ObjectCollection<S2Path> getPaths() {
        return activePaths.values();
    }

    public static Object2BooleanMap.FastEntrySet<BetterBlockPos> getBlocks() {
        return calculatedBlocks.object2BooleanEntrySet();
    }
}
