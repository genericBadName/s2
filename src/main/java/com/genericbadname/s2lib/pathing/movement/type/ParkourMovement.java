package com.genericbadname.s2lib.pathing.movement.type;

import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class ParkourMovement extends WalkMovement {
    @Override
    public void move(Mob mob, BlockPos pos) {
        float yRot = (float) Moves.rotFromPos(mob.blockPosition(), pos);
        Vec3 vec = calculateLaunchVec(mob.position(), Vec3.atCenterOf(pos), 20, -0.08, 0.98);
        mob.setDeltaMovement(vec);

        mob.setYRot(yRot);
        mob.setYHeadRot(yRot);

        //LogManager.getLogger().info("TEST TEST AAA: {}", calculateLaunchVec(new Vec3(2, -58, 1), new Vec3(2, -58, 3), 20, -0.08, 0.98));
    }

    private static Vec3 calculateLaunchVec(Vec3 src, Vec3 dest, double time, double grav, double drag) {
        Vec3 pn = new Vec3(dest.x - src.x, dest.y - src.y, dest.z - src.z);

        if (drag == 1) {
            return new Vec3(
                    pn.x / time,
                    (pn.y - time * grav) / time,
                    pn.z / time
            );
        } else {
            return new Vec3(
                    pn.x / ((1 - Math.pow(drag, time)) / (1 - drag)),
                    (pn.y - (time - (1 - Math.pow(drag, time)) / (1 - drag) * drag) / (1 - drag) * grav) / ((1 - Math.pow(drag, time)) / (1 - drag)),
                    pn.z / ((1 - Math.pow(drag, time)) / (1 - drag))
            );
        }
    }
}
