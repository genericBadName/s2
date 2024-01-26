package com.genericbadname.s2lib.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    // hazard levels
    public static final ForgeConfigSpec.ConfigValue<Double> UNKNOWN_COST_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> PASSABLE_COST_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> WALKABLE_COST_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> POTENTIALLY_AVOID_COST_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> AVOID_COST_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> AVOID_AT_ALL_COSTS_COST_MULTIPLIER;

    // walking
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_WALKING;
    public static final ForgeConfigSpec.ConfigValue<Double> WALK_COST;
    // step up
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_STEP_UP;
    public static final ForgeConfigSpec.ConfigValue<Double> STEP_UP_COST;
    // parkour
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_PARKOUR;
    public static final ForgeConfigSpec.ConfigValue<Double> PARKOUR_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_JUMP_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_JUMP_HEIGHT;
    // free fall
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_FALLING;
    public static final ForgeConfigSpec.ConfigValue<Double> FALL_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_FALL_DISTANCE;
    // general
    public static final ForgeConfigSpec.ConfigValue<Double> COST_INF;
    public static final ForgeConfigSpec.ConfigValue<Double> DIAGONAL_COST_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Integer> TIMEOUT_TIME;

    static {
        BUILDER.push("Pathfinder");

        BUILDER.push("Hazard Levels");
        UNKNOWN_COST_MULTIPLIER = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.hazard_level.unknown_cost_multiplier")
                .comment("Cost multiplier for unknown blocks")
                .define("unknown_cost_multiplier", 1.0);
        PASSABLE_COST_MULTIPLIER = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.hazard_level.passable_cost_multiplier")
                .comment("Cost multiplier for passable blocks (air, grass, etc.)")
                .define("passable_cost_multiplier", 1.0);
        WALKABLE_COST_MULTIPLIER = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.hazard_level.walkable_cost_multiplier")
                .comment("Cost multiplier for walkable (solid) blocks")
                .define("walkable_cost_multiplier", 1.0);
        POTENTIALLY_AVOID_COST_MULTIPLIER = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.hazard_level.potentially_avoid_cost_multiplier")
                .comment("Cost multiplier for potential hazards (water)")
                .define("potential_avoid_cost_multiplier", 5.0);
        AVOID_COST_MULTIPLIER = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.hazard_level.avoid_cost_multiplier")
                .comment("Cost multiplier for hazards (soul sand, magma, etc.)")
                .define("avoid_cost_multiplier", 15.0);
        AVOID_AT_ALL_COSTS_COST_MULTIPLIER = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.hazard_level.avoid_at_all_costs_cost_multiplier")
                .comment("Cost multiplier for extreme hazards (lava)")
                .define("avoid_at_all_costs_multiplier", 50.0);
        BUILDER.pop();

        BUILDER.push("Walking");
        ENABLE_WALKING = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.walking.enable_walking")
                .comment("Whether walking movement is enabled")
                .define("enable_walking", true);
        WALK_COST = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.walking.walk_cost")
                .comment("Cost to perform a walk")
                .define("walk_cost", 1D);
        BUILDER.pop();

        BUILDER.push("Step Up");
        ENABLE_STEP_UP = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.step_up.enable_step_up")
                .comment("Whether step up movement is enabled")
                .define("enable_step_up", true);
        STEP_UP_COST = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.step_up.step_up_cost")
                .comment("Cost to perform a step up")
                .define("step_up_cost", 1.1D);
        BUILDER.pop();

        BUILDER.push("Parkour");
        ENABLE_PARKOUR = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.parkour.enable_parkour")
                .comment("Whether parkour movement is enabled")
                .define("enable_parkour", true);
        PARKOUR_COST = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.parkour.parkour_cost")
                .comment("Cost to perform a parkour jump")
                .define("parkour_cost", 5.0);
        MAX_JUMP_DISTANCE = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.parkour.max_jump_distance")
                .comment("Maximum horizontal jump distance")
                .define("max_jump_distance", 2);
        MAX_JUMP_HEIGHT = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.parkour.max_jump_height")
                .comment("Maximum vertical jump height")
                .define("max_jump_height", 2);
        BUILDER.pop();

        BUILDER.push("Falling");
        ENABLE_FALLING = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.falling.enable_falling")
                .comment("Whether free fall movement is enabled")
                .define("enable_falling", true);
        FALL_COST = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.falling.fall_cost")
                .comment("Cost to perform a free fall")
                .define("fall_cost", 30.0);
        MAX_FALL_DISTANCE = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.falling.max_fall_distance")
                .comment("Maximum allowed falling distance")
                .define("max_fall_distance", 5);
        BUILDER.pop();

        COST_INF = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.cost_inf")
                .comment("The \"effectively infinite\" cost used in pathfinding calculations")
                .define("cost_inf", 100000.0);
        DIAGONAL_COST_MULTIPLIER = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.diagonal_cost_multiplier")
                .comment("Cost multiplier for doing a diagonal move")
                .define("diagonal_cost_multiplier", 1.25);
        TIMEOUT_TIME = BUILDER
                .worldRestart()
                .translation("s2.config.pathfinder.timeout_time")
                .comment("Time (ms) for pathfinder to finish executing before immediately stopping. Use -1 for none")
                .define("timeout_time", 2000);

        SPEC = BUILDER.build();
    }
}
