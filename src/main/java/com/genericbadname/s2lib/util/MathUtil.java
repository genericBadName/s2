package com.genericbadname.s2lib.util;

import com.genericbadname.s2lib.S2Lib;
import net.minecraft.world.phys.Vec3;

// god damn this took way too long to figure out.
public final class MathUtil {
    public static final double POSITION_BUMP_THRESHOLD = 0.05; // how much the travel difference needs to be to increased
    public static final double POSITION_BUMP_AMOUNT = 1.0; // how much is increased

    public static Vec3 calculateLaunchVec(Vec3 src, Vec3 dest, double time, double grav, double drag) {
        double dm = 1-drag; // drag multiplier
        double tx = dest.x - src.x;
        double ty = dest.y - src.y;
        double tz = dest.z - src.z;

        //S2Lib.logInfo("{} & {}", src, dest);

        if (tx > POSITION_BUMP_THRESHOLD) tx += POSITION_BUMP_AMOUNT;
        if (tx < -POSITION_BUMP_THRESHOLD) tx -= POSITION_BUMP_AMOUNT;
        if (tz > POSITION_BUMP_THRESHOLD) tz += POSITION_BUMP_AMOUNT;
        if (tz < -POSITION_BUMP_THRESHOLD) tz -= POSITION_BUMP_AMOUNT;

        //S2Lib.logInfo("after {}, {}, {}", tx, ty, tz);

        return new Vec3(
                -(tx * Math.log(dm)) / dm,
                grav * dm * ((1-Math.pow(dm,time)) / drag),
                -(tz * Math.log(dm)) / dm
        );
    }
}
