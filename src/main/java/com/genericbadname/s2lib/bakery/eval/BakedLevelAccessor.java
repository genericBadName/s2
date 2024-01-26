package com.genericbadname.s2lib.bakery.eval;

import com.genericbadname.s2lib.config.ServerConfig;
import com.genericbadname.s2lib.data.tag.ModBlockTags;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;

public class BakedLevelAccessor {
    private final Level level;

    public BakedLevelAccessor(Level level) {
        this.level = level;
    }

    // TODO: use caching system
    public HazardLevel getHazardLevel(BetterBlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if (state.is(ModBlockTags.PASSABLE)) return HazardLevel.PASSABLE;
        if (state.is(ModBlockTags.WALKABLE)) return HazardLevel.WALKABLE;
        if (state.is(ModBlockTags.AVOID)) return HazardLevel.AVOID;
        if (state.is(ModBlockTags.POTENTIALLY_AVOID)) return HazardLevel.POTENTIALLY_AVOID;
        if (state.is(ModBlockTags.AVOID_AT_ALL_COSTS)) return HazardLevel.AVOID_AT_ALL_COSTS;
        return HazardLevel.UNKNOWN;
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

    @Deprecated
    public Level getLevel() {
        return level;
    }
    public MinecraftServer getServer() {
        return level.getServer();
    }

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
    }

    @Override
    public String toString() {
        return "BakedLevelAccessor{" +
                "level=" + level +
                '}';
    }
}
