package com.genericbadname.s2lib.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    // jumping (parkour)
    public static final ForgeConfigSpec.ConfigValue<Boolean> DEBUG_LOGGING;

    static {
        BUILDER.push("S2 Configs");

        DEBUG_LOGGING = BUILDER.define("debug_logging", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
