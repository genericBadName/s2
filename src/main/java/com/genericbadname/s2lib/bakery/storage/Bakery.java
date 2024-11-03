package com.genericbadname.s2lib.bakery.storage;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.bakery.HazardLevel;
import com.genericbadname.s2lib.data.tag.ModBlockTags;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.google.common.io.Files;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

public class Bakery {
    private final ServerLevel level;
    private final Long2ObjectLinkedOpenHashMap<Loaf> loafMap = new Long2ObjectLinkedOpenHashMap<>();
    private final String basePath;

    // .minecraft/saves/SAVENAME/s2bakery/NAMESPACE/DIMENSION/rX.rZ/cX.cZ.s2loaf
    public static final String BAKERY_PATH = "s2bakery";
    public static final String LOAF_EXTENSION = "s2loaf";

    private static final FilenameFilter filter = (dir, name) -> name.endsWith(LOAF_EXTENSION);

    public Bakery(ServerLevel level) {
        this.level = level;
        this.basePath = level.getServer().getWorldPath(LevelResource.ROOT) + File.separator + BAKERY_PATH;
    }

    // loads bakery from disk to memory for this dimension
    // TODO: acquire lock on file(s) to prevent concurrent shenanigans
    public void readAll() {
        ResourceLocation dimension = level.dimension().location();
        String dimPath = basePath + File.separator + dimension.getNamespace() + File.separator + dimension.getPath();

        File dimDir = new File(dimPath);
        dimDir.mkdirs();

        if (!dimDir.isDirectory()) return;

        File[] regions = dimDir.listFiles();
        if (regions == null) return;

        S2Lib.logInfo("Loading bakery for {} from {}", dimension, dimPath);
        for (File region : regions) {
            if (!region.isDirectory()) continue;
            File[] chunks = region.listFiles(filter);
            if (chunks == null) continue;

            for (File chunk : chunks) {
                ChunkPos pos = parseChunkPos(chunk);
                Loaf loaf = read(chunk);

                if (pos == null || loaf == null) continue;
                loafMap.put(pos.toLong(), loaf);
            }
        }
    }

    private static final int X_ROWS = 16;
    private static final int Z_SIZE = 6;
    private static Loaf read(@NotNull File loafFile) {
        Loaf loaf = null;

        try {
            byte[] bytes = FileUtils.readFileToByteArray(loafFile);

            int height = bytes.length / (X_ROWS * Z_SIZE);
            HazardLevel[][][] hazards = new HazardLevel[height][16][16];

            for (int y=0;y<height;y++) {
                byte[] layer = Arrays.copyOfRange(bytes, X_ROWS * Z_SIZE * y, X_ROWS * Z_SIZE * (y+1));

                for (int x=0;x<X_ROWS;x++) { // get hazard level for this x-row
                    byte[] currentXRow = Arrays.copyOfRange(layer, Z_SIZE * x, Z_SIZE * (x+1));
                    ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);

                    for (int z=0;z<Z_SIZE;z++) { // get the 6 bytes for this row (z-coord)
                        bb.put(currentXRow[z]);
                    }

                    bb.put(new byte[]{0, 0}); // fill last 2 bytes
                    bb.flip();

                    long buf = bb.getLong();

                    // now get hazards
                    for (int z=0;z<16;z++) {
                        int bufSlice = (int) (buf & 0x111);
                        hazards[y][x][z] = HazardLevel.values()[bufSlice];

                        buf = buf << 3;
                    }
                }
            }

            ChunkPos potentialPos = parseChunkPos(loafFile);

            if (potentialPos != null) {
                loaf = new Loaf(hazards, potentialPos);
            }
        } catch (IOException e) {
            S2Lib.LOGGER.error("Encountered an error trying to read from {}: {}", loafFile.getPath(), e.getMessage());
        }

        return loaf;
    }

    // writes current bakery to disk for this dimension
    public void writeAll() {
        ResourceLocation dimension = level.dimension().location();
        String dimPath = basePath + File.separator + dimension.getNamespace() + File.separator + dimension.getPath();

        File dimPathDir = new File(dimPath);
        dimPathDir.mkdirs();

        if (!dimPathDir.isDirectory()) return;

        S2Lib.logInfo("Writing bakery for {} to {}", dimension, dimPath);
        for (Map.Entry<Long, Loaf> entry : loafMap.long2ObjectEntrySet()) {
            ChunkPos pos = new ChunkPos(entry.getKey());
            Loaf loaf = entry.getValue();
            String regionPath = dimPath + File.separator + pos.getRegionX() + "." + pos.getRegionZ();

            new File(regionPath).mkdirs();
            write(loaf, new File(regionPath + File.separator + pos.x + "." + pos.z + "." + LOAF_EXTENSION));
        }
    }

    private static void write(@NotNull Loaf loaf, @NotNull File loafFile) {
        // loaf to byte
        ByteBuffer buffer = ByteBuffer.allocate(6 * 16 * loaf.chunkHazard().length);
        for (HazardLevel[][] hazardLayer : loaf.chunkHazard()) {
            for (int x=0;x<16;x++) { // iterate by X-coordinate. important!!
                buffer.put(HazardLevel.toBytes16(hazardLayer[x]));
            }
        }

        // write loaf file
        try {
            Files.write(buffer.array(), loafFile);
        } catch (IOException e) {
            S2Lib.LOGGER.error("Encountered an error trying to write to {}: {}", loafFile.getPath(), e.getMessage());
        }
    }

    public void reload() {
        S2Lib.logInfo("Reloading bakery for {}", level.dimension().location());

        loafMap.clear();
        readAll();
    }

    public void clear() {
        S2Lib.logInfo("Clearing bakery for {}", level.dimension().location());
        loafMap.clear();
    }

    // TODO: defer chunk scanning and pass to different thread
    // chunk access needs to happen on main thread, but calculations don't
    public Loaf scanChunk(ChunkPos cPos) {
        ChunkAccess chunk = level.getChunk(cPos.x, cPos.z);
        int minHeight = chunk.getMinBuildHeight();
        int totalHeight = chunk.getHeight();

        HazardLevel[][][] hazards = new HazardLevel[totalHeight][16][16];

        for (int y=0;y<totalHeight;y++) {
            for (int x=0;x<16;x++) {
                for (int z=0;z<16;z++) {
                    hazards[y][x][z] = determineHazard(chunk.getBlockState(cPos.getBlockAt(x, minHeight + y, z))); // X ROWS ARE INVERTED
                }
            }
        }

        Loaf scannedLoaf = new Loaf(hazards, cPos);
        loafMap.put(cPos.toLong(), scannedLoaf);

        return scannedLoaf;
    }

    public HazardLevel getHazardLevel(BetterBlockPos pos) {
        Loaf loaf = loafMap.get(new ChunkPos(pos).toLong());

        if (loaf == null) {
            loaf = scanChunk(new ChunkPos(pos));
        }

        return loaf.chunkHazard()[pos.y + 64][pos.x & 0xF][pos.z & 0xF];
    }

    public boolean isPassable(BetterBlockPos pos) {
        return getHazardLevel(pos).equals(HazardLevel.PASSABLE);
    }

    public boolean isWalkable(BetterBlockPos pos) {
        return getHazardLevel(pos).equals(HazardLevel.WALKABLE);
    }

    public boolean isHazardous(BetterBlockPos pos) {
        return getHazardLevel(pos).ordinal() > 2;
    }

    public MinecraftServer getServer() {
        return level.getServer();
    }

    @Override
    public String toString() {
        return "BakedLevelAccessor{" +
                "level=" + level +
                '}';
    }

    private static HazardLevel determineHazard(BlockState state) {
        if (state.is(ModBlockTags.PASSABLE)) return HazardLevel.PASSABLE;
        if (state.is(ModBlockTags.WALKABLE)) return HazardLevel.WALKABLE;
        if (state.is(ModBlockTags.AVOID)) return HazardLevel.AVOID;
        if (state.is(ModBlockTags.POTENTIALLY_AVOID)) return HazardLevel.POTENTIALLY_AVOID;
        if (state.is(ModBlockTags.AVOID_AT_ALL_COSTS)) return HazardLevel.AVOID_AT_ALL_COSTS;

        return HazardLevel.UNKNOWN;
    }

    private static ChunkPos parseChunkPos(String string) {
        String[] parts = string.split("\\.");

        if (parts.length < 2) return null;

        try {
            return new ChunkPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static ChunkPos parseChunkPos(File file) {
        String separator = (SystemUtils.IS_OS_UNIX) ? "/" : "\\\\";
        String[] parts = file.getPath().split(separator);

        return parseChunkPos(parts[parts.length-1]);
    }
}
