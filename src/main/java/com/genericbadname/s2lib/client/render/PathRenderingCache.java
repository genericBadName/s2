package com.genericbadname.s2lib.client.render;

import com.genericbadname.s2lib.pathing.S2Path;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class PathRenderingCache {
    private static final Object2ObjectOpenHashMap<UUID, S2Path> activePaths = new Object2ObjectOpenHashMap<>();

    public static void putPath(UUID uuid, S2Path path) {
        activePaths.put(uuid, path);
    }

    public static void removePath(UUID uuid) {
        activePaths.remove(uuid);
    }

    public static ObjectCollection<S2Path> getPaths() {
        return activePaths.values();
    }
}
