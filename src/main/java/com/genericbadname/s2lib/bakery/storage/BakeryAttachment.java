package com.genericbadname.s2lib.bakery.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface BakeryAttachment {
    Bakery getBakery(ResourceKey<Level> dimension);

    void initBakeries();
    void writeBakeries();
    void clearBakeries();
}
