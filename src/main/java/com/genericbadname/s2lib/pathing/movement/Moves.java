package com.genericbadname.s2lib.pathing.movement;

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
    ASCEND(new VerticalMovement(true, 1), ActionCosts.WALK_ONE_BLOCK_COST.cost, 0, 1, 0),
    DESCEND(new VerticalMovement(false, 1), ActionCosts.WALK_ONE_IN_WATER_COST.cost, 0, -1, 0);

    public final IMovement type;
    public final double cost;
    public final Vec3i offset;

    Moves(IMovement type, double cost, Vec3i offset) {
        this.type = type;
        this.cost = cost;
        this.offset = offset;
    }

    Moves(IMovement type, double cost, int x, int y, int z) {
        this(type, cost, new Vec3i(x, y, z));
    }

    // 4 or 8 directional movement
    public static double rotFromPos(BlockPos pos1, BlockPos pos2) {
        int x = pos2.getX() - pos1.getX();
        int z = pos2.getZ() - pos1.getZ();

        return (x * -90) + (z < 0 ? 180 : 0);
    }
}
