package com.genericbadname.s2lib.pathing.movement;

import com.genericbadname.s2lib.config.ServerConfig;
import com.genericbadname.s2lib.pathing.movement.type.ParkourMovement;
import com.genericbadname.s2lib.pathing.movement.type.VerticalMovement;
import com.genericbadname.s2lib.pathing.movement.type.WalkMovement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

// while this enum looks terrible, it's pretty damn efficient, so I'm not going to give it up
public enum Moves {
    START(new WalkMovement(), 0, 0, 0, 0),
    WALK_NORTH(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, 0, 0, -1),
    WALK_SOUTH(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, 0, 0, 1),
    WALK_EAST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, 1, 0, 0),
    WALK_WEST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, -1, 0, 0),
    WALK_NORTHEAST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, 1, 0, -1),
    WALK_NORTHWEST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, -1, 0, -1),
    WALK_SOUTHEAST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, 1, 0, 1),
    WALK_SOUTHWEST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, -1, 0, 1),
    STEP_DOWN_NORTH(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), 0, -1, -1),
    STEP_DOWN_SOUTH(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), 0, -1, 1),
    STEP_DOWN_EAST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), 1, -1, 0),
    STEP_DOWN_WEST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), -1, -1, 0),
    STEP_DOWN_NORTHEAST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), 1, -1, -1),
    STEP_DOWN_NORTHWEST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), -1, -1, -1),
    STEP_DOWN_SOUTHEAST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), 1, -1, 1),
    STEP_DOWN_SOUTHWEST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), -1, -1, 1),
    STEP_UP_NORTH(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), 0, 1, -1),
    STEP_UP_SOUTH(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), 0, 1, 1),
    STEP_UP_EAST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), 1, 1, 0),
    STEP_UP_WEST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), -1, 1, 0),
    STEP_UP_NORTHEAST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), 1, 1, -1),
    STEP_UP_NORTHWEST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), -1, 1, -1),
    STEP_UP_SOUTHEAST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), 1, 1, 1),
    STEP_UP_SOUTHWEST(new WalkMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost + ServerConfig.DESCEND_COST.get(), -1, 1, 1),
    PARKOUR_NORTH(new ParkourMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost * ServerConfig.JUMP_COST_MULTIPLIER.get(), 0, 0, -1, ServerConfig.MAX_JUMP_DISTANCE.get()+1),
    PARKOUR_SOUTH(new ParkourMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost * ServerConfig.JUMP_COST_MULTIPLIER.get(), 0, 0, 1, ServerConfig.MAX_JUMP_DISTANCE.get()+1),
    PARKOUR_EAST(new ParkourMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost * ServerConfig.JUMP_COST_MULTIPLIER.get(), 1, 0, 0, ServerConfig.MAX_JUMP_DISTANCE.get()+1),
    PARKOUR_WEST(new ParkourMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost * ServerConfig.JUMP_COST_MULTIPLIER.get(), -1, 0, 0, ServerConfig.MAX_JUMP_DISTANCE.get()+1);

    public final IMovement type;
    public final double cost;
    public final Vec3i offset;
    public final int steps;

    Moves(IMovement type, double cost, Vec3i offset) {
        this.type = type;
        this.cost = cost;
        this.offset = offset;
        this.steps = 1;
    }

    Moves(IMovement type, double cost, Vec3i offset, int steps) {
        this.type = type;
        this.cost = cost;
        this.offset = offset;
        this.steps = steps;
    }

    Moves(IMovement type, double cost, int x, int y, int z) {
        this(type, cost, new Vec3i(x, y, z));
    }

    Moves(IMovement type, double cost, int x, int y, int z, int steps) {
        this(type, cost, new Vec3i(x, y, z), steps);
    }

    // 4 or 8 directional movement
    public static double rotFromPos(BlockPos pos1, BlockPos pos2) {
        int x = pos2.getX() - pos1.getX();
        int z = pos2.getZ() - pos1.getZ();

        return (x * -90) + (z < 0 ? 180 : 0);
    }
}
