package com.genericbadname.s2lib.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    // jumping (parkour)
    public static final ForgeConfigSpec.ConfigValue<Double> JUMP_COST_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_JUMP_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_JUMP_HEIGHT;
    // vertical movement (step up/step down)
    public static final ForgeConfigSpec.ConfigValue<Double> DESCEND_COST;
    public static final ForgeConfigSpec.ConfigValue<Double> ASCEND_COST;
    // vertical movement (freefall)
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_FALL_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> FALL_COST;

    static {
        BUILDER.push("S2 Configs");

        JUMP_COST_MULTIPLIER = BUILDER.define("jump_cost_multiplier", 2.0);
        MAX_JUMP_DISTANCE = BUILDER.define("max_jump_distance", 2);
        MAX_JUMP_HEIGHT = BUILDER.define("max_jump_height", 2);

        DESCEND_COST = BUILDER.define("descend_cost", 10.0);
        ASCEND_COST = BUILDER.define("ascend_cost", 10.0);

        MAX_FALL_DISTANCE = BUILDER.define("max_fall_distance", 5);
        FALL_COST = BUILDER.define("fall_cost", 30.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
