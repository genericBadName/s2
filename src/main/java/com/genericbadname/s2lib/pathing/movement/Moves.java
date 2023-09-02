package com.genericbadname.s2lib.pathing.movement;

import com.genericbadname.s2lib.pathing.movement.type.CardinalMovement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

public enum Moves {
    START(new CardinalMovement(), 0, 0, 0, 0),
    WALK_NORTH(new CardinalMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, 0, 0, -1),
    WALK_SOUTH(new CardinalMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, 0, 0, 1),
    WALK_EAST(new CardinalMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, 1, 0, 0),
    WALK_WEST(new CardinalMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, -1, 0, 0),
    WALK_NORTHEAST(new CardinalMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, 1, 0, -1),
    WALK_NORTHWEST(new CardinalMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, -1, 0, -1),
    WALK_SOUTHEAST(new CardinalMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, 1, 0, 1),
    WALK_SOUTHWEST(new CardinalMovement(), ActionCosts.WALK_ONE_BLOCK_COST.cost, -1, 0, 1);

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
