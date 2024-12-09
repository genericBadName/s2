package com.genericbadname.s2lib.bakery.storage;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.bakery.HazardLevel;
import com.genericbadname.s2lib.data.tag.ModBlockTags;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Bakery {
    private final ServerLevel level;
    private final Long2ObjectMap<Loaf> loafMap;
    private final String basePath;
    private final String dimPath;

    // .minecraft/saves/SAVENAME/s2bakery/NAMESPACE/DIMENSION/rX.rZ/cX.cZ.s2loaf
    public static final String BAKERY_PATH = "s2bakery";
    public static final String LOAF_EXTENSION = "s2loaf";

    public Bakery(ServerLevel level) {
        this.level = level;
        this.loafMap = Long2ObjectMaps.synchronize(new Long2ObjectLinkedOpenHashMap<>());
        String root = level.getServer().getWorldPath(LevelResource.ROOT).toString();
        this.basePath = root.substring(0, root.length()-1) + BAKERY_PATH;
        this.dimPath = basePath + File.separator + level.dimension().location().getNamespace() + File.separator + level.dimension().location().getPath();
    }

    // loads bakery from disk to memory for this dimension
    private static final int X_ROWS = 16;
    private static final int Z_SIZE = 6;
    private Loaf read(@NotNull Path loafPath) throws ExecutionException, InterruptedException {
        ChunkPos potentialPos = parseChunkPos(loafPath.toString());
        CompletableFuture<Loaf> future = new CompletableFuture<>();

        if (potentialPos == null) return null;

        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(loafPath)) {
            // acquire lock on file to prevent concurrent modification
            FileLock lock = null;

            try {
                // lock and read
                lock = channel.tryLock();
                ByteBuffer byteBuffer = ByteBuffer.allocate(16 * 16 * level.getHeight());
                channel.read(byteBuffer, 0, null, new CompletionHandler<>() {
                    @Override
                    public void completed(Integer result, Object attachment) {
                        byte[] bytes = byteBuffer.array();

                        // decode bytes
                        int height = bytes.length / (X_ROWS * Z_SIZE);
                        HazardLevel[][][] hazards = new HazardLevel[height][16][16];

                        for (int y=0;y<height;y++) {
                            byte[] layer = Arrays.copyOfRange(bytes, X_ROWS * Z_SIZE * y, X_ROWS * Z_SIZE * (y+1));

                            for (int x=0;x<X_ROWS;x++) { // get hazard level for this x-row
                                byte[] currentXRow = Arrays.copyOfRange(layer, Z_SIZE * x, Z_SIZE * (x+1));
                                hazards[y][x] = HazardLevel.fromBytes16(currentXRow);
                            }
                        }

                        future.completeAsync(() -> new Loaf(hazards, potentialPos), S2Lib.SERVICE);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        S2Lib.LOGGER.error("Failed to read from file {}: {}", loafPath, exc.getMessage());
                    }
                });

            } catch (OverlappingFileLockException e) {
                S2Lib.LOGGER.error("Tried to read from an already locked file {}", loafPath);
            }

            // close resources
            if (lock != null) {
                lock.release();
            }
        } catch (IOException e) {
            S2Lib.LOGGER.error("Encountered an IOException trying to read from {}: {}", loafPath, e.getMessage());
        }

        return future.get();
    }

    public Loaf attemptRead(@NotNull ChunkPos pos) {
        setupDimDir();
        Path potentialPath = Path.of(dimPath + File.separator + pos.getRegionX() + "." + pos.getRegionZ() + File.separator + File.separator + pos.x + "." + pos.z + "." + LOAF_EXTENSION);

        // handle executor shenanigans
        if (potentialPath.toFile().exists()) {
            try {
                return read(potentialPath);
            } catch (ExecutionException | InterruptedException e) {
                S2Lib.LOGGER.error("Execution interrupted with trying to read from {}: {}", potentialPath, e.getMessage());
            }
        }

        return null;
    }

    // writes current bakery to disk for this dimension
    public void writeAll() {
        setupDimDir();

        S2Lib.logInfo("Writing bakery to {}", dimPath);
        for (Map.Entry<Long, Loaf> entry : loafMap.long2ObjectEntrySet()) {
            ChunkPos pos = new ChunkPos(entry.getKey());
            Loaf loaf = entry.getValue();
            String regionPath = dimPath + File.separator + pos.getRegionX() + "." + pos.getRegionZ();

            new File(regionPath).mkdirs();
            CompletableFuture.runAsync(() -> write(loaf, Path.of(regionPath + File.separator + pos.x + "." + pos.z + "." + LOAF_EXTENSION)), S2Lib.SERVICE);
        }
    }

    // async write
    private static void write(@NotNull Loaf loaf, @NotNull Path loafPath) {
        // loaf to byte
        ByteBuffer buffer = ByteBuffer.allocate(6 * 16 * loaf.chunkHazard().length);
        HazardLevel[][][] hazard = loaf.chunkHazard();

        for (HazardLevel[][] yLayer : hazard) {
            for (int x = 0; x < 16; x++) { // iterate by X-coordinate. important!!
                buffer.put(HazardLevel.toBytes16(yLayer[x]));
            }
        }

        // write loaf file
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(loafPath, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            // acquire lock on file to prevent concurrent modification
            buffer.position(0);
            channel.tryLock(0, buffer.capacity(), false);
            channel.write(buffer, 0, buffer, new CompletionHandler<>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    buffer.clear();
                    //S2Lib.LOGGER.info("Wrote {} bytes to {}", result, loafPath);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {

                }
            });
        } catch (IOException e) {
            S2Lib.LOGGER.error("Encountered an IO exception trying to write to {}: {}", loafPath, e.getMessage());
        } catch (OverlappingFileLockException e) {
            S2Lib.LOGGER.error("Tried to write to an already locked file {}: {}", loafPath, e.getMessage());
        }
    }

    private File setupDimDir() {
        File dimDir = new File(dimPath);
        dimDir.mkdirs();

        return dimDir;
    }

    public void clear() {
        S2Lib.logInfo("Clearing bakery for {}", level.dimension().location());
        loafMap.clear();
    }

    // TODO: defer chunk scanning and pass to different thread
    // chunk access needs to happen on main thread, but calculations don't
    public Loaf scanChunk(ChunkPos cPos) {
        return scanChunk(level.getChunk(cPos.x, cPos.z));
    }

    public Loaf scanChunk(ChunkAccess chunkAccess) {
        ChunkPos cPos = chunkAccess.getPos();
        int minHeight = chunkAccess.getMinBuildHeight();
        int totalHeight = chunkAccess.getHeight();

        HazardLevel[][][] hazards = new HazardLevel[totalHeight][16][16];

        for (int y=0;y<totalHeight;y++) {
            for (int x=0;x<16;x++) {
                for (int z=0;z<16;z++) {
                    hazards[y][x][z] = determineHazard(chunkAccess.getBlockState(cPos.getBlockAt(x, minHeight + y, z)));
                }
            }
        }

        Loaf scannedLoaf = new Loaf(hazards, cPos);
        loafMap.put(cPos.toLong(), scannedLoaf);

        return scannedLoaf;
    }

    public void updateHazardLevel(BetterBlockPos pos, BlockState newState) {
        ChunkPos cPos = new ChunkPos(pos);
        Loaf loaf = loafMap.get(cPos.toLong());

        if (loaf == null) {
            Loaf attempted = attemptRead(cPos);

            if (attempted != null) {
                loaf = attempted;
            } else {
                scanChunk(cPos);
                return;
            }
        }

        loaf.chunkHazard()[pos.y + 64][pos.x & 0xF][pos.z & 0xF] = determineHazard(newState);
        loafMap.put(cPos.toLong(), loaf);
        S2Lib.logInfo("Updated hazard at {}", pos);
    }

    public HazardLevel getHazardLevel(BetterBlockPos pos) {
        ChunkPos cPos = new ChunkPos(pos);
        Loaf loaf = loafMap.get(cPos.toLong());

        if (loaf == null) loaf = attemptRead(cPos); // try and see if file already exists
        if (loaf == null) loaf = scanChunk(cPos); // if not, just scan the chunk

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
