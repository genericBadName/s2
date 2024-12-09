package com.genericbadname.s2lib.mixin.chunk;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.bakery.storage.Bakery;
import com.genericbadname.s2lib.bakery.storage.BakeryAttachment;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Set;

@Mixin(MinecraftServer.class)
public abstract class MixinBakeryState implements BakeryAttachment {
    @Shadow public abstract Set<ResourceKey<Level>> levelKeys();
    @Shadow @Final private Map<ResourceKey<Level>, ServerLevel> levels;

    @Unique
    private final Map<ResourceKey<Level>, Bakery> bakeries = Maps.newLinkedHashMap();

    @Override
    public void initBakeries() {
        S2Lib.LOGGER.info("Initializing bakeries");
        for (ResourceKey<Level> key : levelKeys()) {
            S2Lib.LOGGER.info("Initializing bakery for {}", key.location());
            bakeries.put(key, new Bakery(levels.get(key)));
        }
    }

    @Override
    public Bakery getBakery(ResourceKey<Level> dimension) {
        return bakeries.get(dimension);
    }

    @Override
    public void writeBakeries() {
        S2Lib.LOGGER.info("Writing bakeries to disk");
        for (Bakery bakery : bakeries.values()) {
            bakery.writeAll();
        }
    }

    @Override
    public void clearBakeries() {
        S2Lib.LOGGER.info("Clearing bakeries from memory");
        for (Bakery bakery : bakeries.values()) {
            bakery.clear();
        }
    }
}
