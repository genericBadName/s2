package com.genericbadname.s2lib.pathing.movement;

import com.genericbadname.s2lib.bakery.storage.Bakery;
import com.genericbadname.s2lib.config.ServerConfig;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.movement.type.FallMovement;
import com.genericbadname.s2lib.pathing.movement.type.ParkourMovement;
import com.genericbadname.s2lib.pathing.movement.type.WalkMovement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

import java.util.function.BiFunction;

import static com.genericbadname.s2lib.pathing.movement.PositionValidity.PositionValidator;

// while this enum looks terrible, it's pretty damn efficient, so I'm not going to give it up
public enum Moves {
    START(new Builder(new WalkMovement(), 0)
            .offset(0, 0, 0)
            .positionValidator(PositionValidator::cardinal)
    ),
    WALK_NORTH(new Builder(new WalkMovement(), ServerConfig.WALK_COST.get())
            .offset(0, 0, -1)
            .positionValidator(PositionValidator::cardinal)
    ),
    WALK_SOUTH(new Builder(new WalkMovement(), ServerConfig.WALK_COST.get())
            .offset(0, 0, 1)
            .positionValidator(PositionValidator::cardinal)
    ),
    WALK_EAST(new Builder(new WalkMovement(), ServerConfig.WALK_COST.get())
            .offset(1, 0, 0)
            .positionValidator(PositionValidator::cardinal)
    ),
    WALK_WEST(new Builder(new WalkMovement(), ServerConfig.WALK_COST.get())
            .offset(-1, 0, 0)
            .positionValidator(PositionValidator::cardinal)
    ),
    WALK_NORTHEAST(new Builder(new WalkMovement(), ServerConfig.WALK_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(1, 0, -1)
            .positionValidator(PositionValidator::northeast)
    ),
    WALK_NORTHWEST(new Builder(new WalkMovement(), ServerConfig.WALK_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(-1, 0, -1)
            .positionValidator(PositionValidator::northwest)
    ),
    WALK_SOUTHEAST(new Builder(new WalkMovement(), ServerConfig.WALK_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(1, 0, 1)
            .positionValidator(PositionValidator::southeast)
    ),
    WALK_SOUTHWEST(new Builder(new WalkMovement(), ServerConfig.WALK_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(-1, 0, 1)
            .positionValidator(PositionValidator::southwest)
    ),
    STEP_UP_NORTH(new Builder(new WalkMovement(), ServerConfig.STEP_UP_COST.get())
            .offset(0, 1, -1)
            .positionValidator(PositionValidator::cardinal)
    ),
    STEP_UP_SOUTH(new Builder(new WalkMovement(), ServerConfig.STEP_UP_COST.get())
            .offset(0, 1, 1)
            .positionValidator(PositionValidator::cardinal)
    ),
    STEP_UP_EAST(new Builder(new WalkMovement(), ServerConfig.STEP_UP_COST.get())
            .offset(1, 1, 0)
            .positionValidator(PositionValidator::cardinal)
    ),
    STEP_UP_WEST(new Builder(new WalkMovement(), ServerConfig.STEP_UP_COST.get())
            .offset(-1, 1, 0)
            .positionValidator(PositionValidator::cardinal)
    ),
    STEP_UP_NORTHEAST(new Builder(new WalkMovement(), ServerConfig.STEP_UP_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(1, 1, -1)
            .positionValidator(PositionValidator::northeast)
    ),
    STEP_UP_NORTHWEST(new Builder(new WalkMovement(), ServerConfig.STEP_UP_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(-1, 1, -1)
            .positionValidator(PositionValidator::northwest)
    ),
    STEP_UP_SOUTHEAST(new Builder(new WalkMovement(), ServerConfig.STEP_UP_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(1, 1, 1)
            .positionValidator(PositionValidator::southeast)
    ),
    STEP_UP_SOUTHWEST(new Builder(new WalkMovement(), ServerConfig.STEP_UP_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(-1, 1, 1)
            .positionValidator(PositionValidator::southwest)
    ),
    PARKOUR_NORTH(new Builder(new ParkourMovement(), ServerConfig.PARKOUR_COST.get())
            .offset(0, 0, -1)
            .positionValidator(PositionValidator::cardinal)
            .steps(ServerConfig.MAX_JUMP_DISTANCE.get())
            .stepVec(0, 0, -1)
            .stopCondition(PositionValidity.FAIL_BLOCKED)
    ),
    PARKOUR_SOUTH(new Builder(new ParkourMovement(), ServerConfig.PARKOUR_COST.get())
            .offset(0, 0, 1)
            .positionValidator(PositionValidator::cardinal)
            .steps(ServerConfig.MAX_JUMP_DISTANCE.get())
            .stepVec(0, 0, 1)
            .stopCondition(PositionValidity.FAIL_BLOCKED)
    ),
    PARKOUR_EAST(new Builder(new ParkourMovement(), ServerConfig.PARKOUR_COST.get())
            .offset(1, 0, 0)
            .positionValidator(PositionValidator::cardinal)
            .steps(ServerConfig.MAX_JUMP_DISTANCE.get())
            .stepVec(1, 0, 0)
            .stopCondition(PositionValidity.FAIL_BLOCKED)
    ),
    PARKOUR_WEST(new Builder(new ParkourMovement(), ServerConfig.PARKOUR_COST.get())
            .offset(-1, 0, 0)
            .positionValidator(PositionValidator::cardinal)
            .steps(ServerConfig.MAX_JUMP_DISTANCE.get())
            .stepVec(-1, 0, 0)
            .stopCondition(PositionValidity.FAIL_BLOCKED)
    ),
    PARKOUR_NORTHEAST(new Builder(new ParkourMovement(), ServerConfig.PARKOUR_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(1, 0, -1)
            .positionValidator(PositionValidator::northeast)
            .steps(ServerConfig.MAX_JUMP_DISTANCE.get())
            .stepVec(1, 0, -1)
            .stopCondition(PositionValidity.FAIL_BLOCKED)
    ),
    PARKOUR_NORTHWEST(new Builder(new ParkourMovement(), ServerConfig.PARKOUR_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(-1, 0, -1)
            .positionValidator(PositionValidator::northwest)
            .steps(ServerConfig.MAX_JUMP_DISTANCE.get()).
            stepVec(-1, 0, -1)
            .stopCondition(PositionValidity.FAIL_BLOCKED)
    ),
    PARKOUR_SOUTHEAST(new Builder(new ParkourMovement(), ServerConfig.PARKOUR_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(1, 0, 1)
            .positionValidator(PositionValidator::southeast)
            .steps(ServerConfig.MAX_JUMP_DISTANCE.get())
            .stepVec(1, 0, 1)
            .stopCondition(PositionValidity.FAIL_BLOCKED)
    ),
    PARKOUR_SOUTHWEST(new Builder(new ParkourMovement(), ServerConfig.PARKOUR_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(-1, 0, 1)
            .positionValidator(PositionValidator::southwest)
            .steps(ServerConfig.MAX_JUMP_DISTANCE.get())
            .stepVec(-1, 0, 1)
            .stopCondition(PositionValidity.FAIL_BLOCKED)
    ),
    FALL_NORTH(new Builder(new FallMovement(), ServerConfig.FALL_COST.get())
            .offset(0, -1, -1)
            .positionValidator(PositionValidator::cardinal)
            .steps(ServerConfig.MAX_FALL_DISTANCE.get())
            .stepVec(0, -1, 0)
    ),
    FALL_SOUTH(new Builder(new FallMovement(), ServerConfig.FALL_COST.get())
            .offset(0, -1, 1)
            .positionValidator(PositionValidator::cardinal)
            .steps(ServerConfig.MAX_FALL_DISTANCE.get())
            .stepVec(0, -1, 0)
    ),
    FALL_EAST(new Builder(new FallMovement(), ServerConfig.FALL_COST.get())
            .offset(1, -1, 0)
            .positionValidator(PositionValidator::cardinal)
            .steps(ServerConfig.MAX_FALL_DISTANCE.get())
            .stepVec(0, -1, 0)
    ),
    FALL_WEST(new Builder(new FallMovement(), ServerConfig.FALL_COST.get())
            .offset(-1, -1, 0)
            .positionValidator(PositionValidator::cardinal)
            .steps(ServerConfig.MAX_FALL_DISTANCE.get())
            .stepVec(0, -1, 0)
    ),
    FALL_NORTHEAST(new Builder(new FallMovement(), ServerConfig.FALL_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(1, -1, -1)
            .positionValidator(PositionValidator::northeast)
            .steps(ServerConfig.MAX_FALL_DISTANCE.get())
            .stepVec(0, -1, 0)
    ),
    FALL_NORTHWEST(new Builder(new FallMovement(), ServerConfig.FALL_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(-1, -1, 1)
            .positionValidator(PositionValidator::northwest)
            .steps(ServerConfig.MAX_FALL_DISTANCE.get())
            .stepVec(0, -1, 0)
    ),
    FALL_SOUTHEAST(new Builder(new FallMovement(), ServerConfig.FALL_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(1, -1, 1)
            .positionValidator(PositionValidator::southeast)
            .steps(ServerConfig.MAX_FALL_DISTANCE.get())
            .stepVec(0, -1, 0)
    ),
    FALL_SOUTHWEST(new Builder(new FallMovement(), ServerConfig.FALL_COST.get() * ServerConfig.DIAGONAL_COST_MULTIPLIER.get())
            .offset(-1, -1, 1)
            .positionValidator(PositionValidator::southwest)
            .steps(ServerConfig.MAX_FALL_DISTANCE.get())
            .stepVec(0, -1, 0)
    );

    public final IMovement type;
    public final double cost;
    public final Vec3i offset;
    public final int steps;
    public final Vec3i stepVec;
    public final PositionValidity stopCondition;
    public final BiFunction<Bakery, BetterBlockPos, PositionValidity> positionValidator;

    Moves(Builder builder) {
        this.type = builder.type;
        this.cost = builder.cost;
        this.offset = builder.offset;
        this.steps = builder.steps;
        this.stepVec = builder.stepVec;
        this.stopCondition = builder.stopCondition;
        this.positionValidator = builder.positionValidator;
    }

    private static class Builder {
        private final IMovement type;
        private final double cost;
        private Vec3i offset = Vec3i.ZERO;
        private int steps = 1;
        private Vec3i stepVec = Vec3i.ZERO;
        private PositionValidity stopCondition = PositionValidity.SUCCESS;
        private BiFunction<Bakery, BetterBlockPos, PositionValidity> positionValidator = (a, b) -> PositionValidity.FAIL_UNKNOWN;

        public Builder(IMovement type, double cost) {
            this.type = type;
            this.cost = cost;
        }

        public Builder offset(Vec3i offset) {
            this.offset = offset;
            return this;
        }

        public Builder offset(int x, int y, int z) {
            this.offset = new Vec3i(x, y, z);
            return this;
        }

        public Builder steps(int steps) {
            this.steps = steps;
            return this;
        }

        public Builder stepVec(Vec3i stepVec) {
            this.stepVec = stepVec;
            return this;
        }

        public Builder stepVec(int sx, int sy, int sz) {
            this.stepVec = new Vec3i(sx, sy, sz);
            return this;
        }

        public Builder stopCondition(PositionValidity stopCondition) {
            this.stopCondition = stopCondition;
            return this;
        }

        public Builder positionValidator(BiFunction<Bakery, BetterBlockPos, PositionValidity> positionValidator) {
            this.positionValidator = positionValidator;
            return this;
        }
    }

    // 4 or 8 directional movement
    public static double rotFromPos(BlockPos pos1, BlockPos pos2) {
        int x = pos2.getX() - pos1.getX();
        int z = pos2.getZ() - pos1.getZ();

        return (x * -90) + (z < 0 ? 180 : 0);
    }

    public static Moves[] walking() {
        return new Moves[] {
                WALK_NORTH,
                WALK_SOUTH,
                WALK_EAST,
                WALK_WEST,
                WALK_NORTHEAST,
                WALK_NORTHWEST,
                WALK_SOUTHEAST,
                WALK_SOUTHWEST
        };
    }

    public static Moves[] stepUp() {
        return new Moves[] {
                STEP_UP_NORTH,
                STEP_UP_SOUTH,
                STEP_UP_EAST,
                STEP_UP_WEST,
                STEP_UP_NORTHEAST,
                STEP_UP_NORTHWEST,
                STEP_UP_SOUTHEAST,
                STEP_UP_SOUTHWEST
        };
    }

    public static Moves[] parkour() {
        return new Moves[] {
                PARKOUR_NORTH,
                PARKOUR_SOUTH,
                PARKOUR_EAST,
                PARKOUR_WEST,
                PARKOUR_NORTHEAST,
                PARKOUR_NORTHWEST,
                PARKOUR_SOUTHEAST,
                PARKOUR_SOUTHWEST
        };
    }

    public static Moves[] falling() {
        return new Moves[] {
                FALL_NORTH,
                FALL_SOUTH,
                FALL_EAST,
                FALL_WEST,
                FALL_NORTHEAST,
                FALL_NORTHWEST,
                FALL_SOUTHEAST,
                FALL_SOUTHWEST
        };
    }
}
