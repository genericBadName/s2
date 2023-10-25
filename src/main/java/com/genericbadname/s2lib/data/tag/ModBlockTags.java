package com.genericbadname.s2lib.data.tag;

import com.genericbadname.s2lib.S2Lib;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> SOLID = TagKey.create(Registry.BLOCK_REGISTRY, S2Lib.asResource("solid"));
    public static final TagKey<Block> PASSABLE = TagKey.create(Registry.BLOCK_REGISTRY, S2Lib.asResource("passable"));
}
