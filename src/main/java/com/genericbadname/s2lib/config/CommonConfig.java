package com.genericbadname.s2lib.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    // jumping (parkour)
    public static final ForgeConfigSpec.ConfigValue<Boolean> DEBUG_LOGGING;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DEBUG_ENTITY_PATHS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DEBUG_PATH_CALCULATIONS;

    static {
        BUILDER.push("Debug Configs");

        DEBUG_LOGGING = BUILDER.define("debug_logging", false);
        DEBUG_ENTITY_PATHS = BUILDER.define("debug_entity_paths", false);
        DEBUG_PATH_CALCULATIONS = BUILDER.define("debug_path_calculations", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
