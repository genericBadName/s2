package com.genericbadname.s2lib.pathing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

/**
 * A better BlockPos that has fewer hash collisions (and slightly more performant offsets)
 * <p>
 * Is it really faster to subclass BlockPos and calculate a hash in the constructor like this, taking everything into account?
 * Yes. 20% faster actually. It's called BETTER BlockPos for a reason. Source:
 * <a href="https://docs.google.com/spreadsheets/d/1GWjOjOZINkg_0MkRgKRPH1kUzxjsnEROD9u3UFh_DJc">Benchmark Spreadsheet</a>
 *
 * @author leijurv
 */
public final class BetterBlockPos extends BlockPos {

    public static final BetterBlockPos ORIGIN = new BetterBlockPos(0, 0, 0);

    public final int x;
    public final int y;
    public final int z;

    public BetterBlockPos(int x, int y, int z) {
        super(x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BetterBlockPos(double x, double y, double z) {
        this(Mth.floor(x), Mth.floor(y), Mth.floor(z));
    }

    public BetterBlockPos(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Like constructor but returns null if pos is null, good if you just need to possibly censor coordinates
     *
     * @param pos The BlockPos, possibly null, to convert
     * @return A BetterBlockPos or null if pos was null
     */
    public static BetterBlockPos from(BlockPos pos) {
        if (pos == null) {
            return null;
        }

        return new BetterBlockPos(pos);
    }

    @Override
    public int hashCode() {
        return (int) longHash(x, y, z);
    }

    public static long longHash(BetterBlockPos pos) {
        return longHash(pos.x, pos.y, pos.z);
    }

    public static long longHash(int x, int y, int z) {
        // invertibility would be incredibly useful
        /*
         *   This is the hashcode implementation of Vec3i (the superclass of the class which I shall not name)
         *
         *   public int hashCode() {
         *       return (this.getY() + this.getZ() * 31) * 31 + this.getX();
         *   }
         *
         *   That is terrible and has tons of collisions and makes the HashMap terribly inefficient.
         *
         *   That's why we grab out the X, Y, Z and calculate our own hashcode
         */
        long hash = 3241;
        hash = 3457689L * hash + x;
        hash = 8734625L * hash + y;
        hash = 2873465L * hash + z;
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof BetterBlockPos) {
            BetterBlockPos oth = (BetterBlockPos) o;
            return oth.x == x && oth.y == y && oth.z == z;
        }
        // during path execution, like "if (whereShouldIBe.equals(whereAmI)) {"
        // sometimes we compare a BlockPos to a BetterBlockPos
        BlockPos oth = (BlockPos) o;
        return oth.getX() == x && oth.getY() == y && oth.getZ() == z;
    }

    @Override
    public BetterBlockPos above() {
        // this is unimaginably faster than blockpos.up
        // that literally calls
        // this.up(1)
        // which calls this.offset(Direction.UP, 1)
        // which does return n == 0 ? this : new BlockPos(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n);

        // how many function calls is that? up(), up(int), offset(Direction, int), new BlockPos, getX, getXOffset, getY, getYOffset, getZ, getZOffset
        // that's ten.
        // this is one function call.
        return new BetterBlockPos(x, y + 1, z);
    }

    @Override
    public BetterBlockPos above(int amt) {
        // see comment in up()
        return amt == 0 ? this : new BetterBlockPos(x, y + amt, z);
    }

    @Override
    public BetterBlockPos below() {
        // see comment in up()
        return new BetterBlockPos(x, y - 1, z);
    }

    @Override
    public BetterBlockPos below(int amt) {
        // see comment in up()
        return amt == 0 ? this : new BetterBlockPos(x, y - amt, z);
    }

    @Override
    public BetterBlockPos relative(Direction dir) {
        Vec3i vec = dir.getNormal();
        return new BetterBlockPos(x + vec.getX(), y + vec.getY(), z + vec.getZ());
    }

    @Override
    public BetterBlockPos relative(Direction dir, int dist) {
        if (dist == 0) {
            return this;
        }
        Vec3i vec = dir.getNormal();
        return new BetterBlockPos(x + vec.getX() * dist, y + vec.getY() * dist, z + vec.getZ() * dist);
    }

    @Override
    public BetterBlockPos north() {
        return new BetterBlockPos(x, y, z - 1);
    }

    @Override
    public BetterBlockPos north(int amt) {
        return amt == 0 ? this : new BetterBlockPos(x, y, z - amt);
    }

    @Override
    public BetterBlockPos south() {
        return new BetterBlockPos(x, y, z + 1);
    }

    @Override
    public BetterBlockPos south(int amt) {
        return amt == 0 ? this : new BetterBlockPos(x, y, z + amt);
    }

    @Override
    public BetterBlockPos east() {
        return new BetterBlockPos(x + 1, y, z);
    }

    @Override
    public BetterBlockPos east(int amt) {
        return amt == 0 ? this : new BetterBlockPos(x + amt, y, z);
    }

    @Override
    public BetterBlockPos west() {
        return new BetterBlockPos(x - 1, y, z);
    }

    @Override
    public BetterBlockPos west(int amt) {
        return amt == 0 ? this : new BetterBlockPos(x - amt, y, z);
    }

    @Override
    public String toString() {
        return String.format("BetterBlockPos{x=%s,y=%s,z=%s}", x, y, z);
    }

    @Override
    public BetterBlockPos offset(Vec3i vector) {
        return from(super.offset(vector));
    }

    @Override
    public BetterBlockPos offset(int dx, int dy, int dz) {
        return from(super.offset(dx, dy, dz));
    }

    @Override
    public BetterBlockPos offset(double dx, double dy, double dz) {
        return from(super.offset(dx, dy, dz));
    }
}
