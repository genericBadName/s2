package com.genericbadname.s2lib.pathing.entity;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.pathing.AStarPathCalculator;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Node;
import com.genericbadname.s2lib.pathing.S2Path;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public abstract class S2Mob extends PathfinderMob {
    public static final int RETRY_UPDATE_COOLDOWN = 20; // ticks
    public static final int FAIL_UPDATE_PENALTY = 60; // ticks
    private int updateTimer = RETRY_UPDATE_COOLDOWN;
    private final AStarPathCalculator calculator;
    private S2Path path;
    private BetterBlockPos lastTracked;
    public S2Mob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.calculator = new AStarPathCalculator(level);
        this.path = new S2Path();
        this.lastTracked = BetterBlockPos.ORIGIN;
    }

    public AStarPathCalculator getCalculator() {
        return calculator;
    }

    @Override
    public void tick() {
        super.tick();

        if (updateTimer > 0) updateTimer--;
    }

    public S2Path getPath() {
        return path;
    }
    public S2Path calculateFromCurrentLocation(BetterBlockPos dest) {
        path = calculator.calculate(BetterBlockPos.from(blockPosition()), dest);

        if (!path.isPossible()) updateTimer = FAIL_UPDATE_PENALTY; // stop constant failure updates

        // DEBUG
        //level.setBlockAndUpdate(blockPosition(), Blocks.RED_STAINED_GLASS.defaultBlockState());
        //level.setBlockAndUpdate(dest, Blocks.GREEN_STAINED_GLASS.defaultBlockState());

        return path;
    }

    public void updatePath() {
        if (updateTimer != 0) return;

        LivingEntity target = getTarget();
        updateTimer = RETRY_UPDATE_COOLDOWN;
        path = null;

        // update path according to target position
        if (target != null) {
            if (!target.blockPosition().equals(lastTracked)) {
                S2Lib.logInfo("Updating path for {}", this);

                lastTracked = BetterBlockPos.from(target.blockPosition());
                path = calculateFromCurrentLocation(lastTracked);
                //target.level.setBlockAndUpdate(lastTracked, Blocks.GREEN_STAINED_GLASS.defaultBlockState());
            }
        }
    }
}
