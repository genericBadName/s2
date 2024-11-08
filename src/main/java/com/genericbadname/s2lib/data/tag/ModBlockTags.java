package com.genericbadname.s2lib.data.tag;

import com.genericbadname.s2lib.S2Lib;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> PASSABLE = TagKey.create(Registries.BLOCK, S2Lib.asResource("passable"));
    public static final TagKey<Block> WALKABLE = TagKey.create(Registries.BLOCK, S2Lib.asResource("walkable"));
    public static final TagKey<Block> POTENTIALLY_AVOID = TagKey.create(Registries.BLOCK, S2Lib.asResource("potentially_avoid"));
    public static final TagKey<Block> AVOID = TagKey.create(Registries.BLOCK, S2Lib.asResource("avoid"));
    public static final TagKey<Block> AVOID_AT_ALL_COSTS = TagKey.create(Registries.BLOCK, S2Lib.asResource("avoid_at_all_costs"));
}
