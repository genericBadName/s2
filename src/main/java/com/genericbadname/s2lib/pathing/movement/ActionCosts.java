package com.genericbadname.s2lib.pathing.movement;

public enum ActionCosts {
    WALK_ONE_BLOCK_COST(20 / 4.317), // 4.633
    WALK_ONE_IN_WATER_COST(20 / 2.2), // 9.091
    WALK_ONE_OVER_SOUL_SAND_COST(WALK_ONE_BLOCK_COST.cost * 2), // 0.4 in BlockSoulSand but effectively about half
    LADDER_UP_ONE_COST(20 / 2.35), // 8.511
    LADDER_DOWN_ONE_COST(20 / 3.0), // 6.667
    SNEAK_ONE_BLOCK_COST(20 / 1.3), // 15.385
    SPRINT_ONE_BLOCK_COST(20 / 5.612), // 3.564
    SPRINT_MULTIPLIER(SPRINT_ONE_BLOCK_COST.cost / WALK_ONE_BLOCK_COST.cost), // 0.769
    COST_INF(1000000);

    public final double cost;

    ActionCosts(double cost) {
        this.cost = cost;
    }
}
