package com.genericbadname.s2lib.pathing.movement;

import com.genericbadname.s2lib.pathing.movement.type.CardinalMovement;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public enum Moves {
    CARDINAL {
        @Override
        public IMovement get() {
            return new CardinalMovement();
        }
    };

    public abstract IMovement get();

    public static int calculateCost(BlockPos self, BlockPos other) {
        return Mth.abs(self.getX() - other.getX()) + Mth.abs(self.getZ() - other.getZ());
    }

    // 4 or 8 directional movement
    public static double rotFromPos(BlockPos pos1, BlockPos pos2) {
        int x = pos2.getX() - pos1.getX();
        int z = pos2.getZ() - pos1.getZ();

        return (x * -90) + (z < 0 ? 180 : 0);
    }
}
