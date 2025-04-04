package com.genericbadname.s2lib.example.goal;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Node;
import com.genericbadname.s2lib.pathing.S2Path;
import com.genericbadname.s2lib.pathing.entity.S2SmartMob;
import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class S2GetToBlockGoal<M extends Mob & S2SmartMob> extends Goal {
    private final M mob;
    private final S2Node target;
    private List<S2Node> path;
    private S2Node walkingTo;

    public S2GetToBlockGoal(M mob, BetterBlockPos target) {
        this.mob = mob;
        this.target = new S2Node(target, Moves.START);

        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void stop() {
        walkingTo = null;
        path = null;

        mob.getNavigation().stop();
    }

    @Override
    public void start() {
        try {
            CompletableFuture<Void> pathFuture = mob.s2$calculateFromCurrentLocation(target.getPos());

            pathFuture.thenRun(() -> {
                if (mob.s2$getPotentialPath() == null) return;
                path = mob.s2$getPotentialPath().getNodes();
                walkingTo = path.get(0);
            });
        } catch (IOException e) {
            S2Lib.LOGGER.error("Entity {} could not access its level.", mob.getUUID());
        }
    }

    @Override
    public void tick() {
        if (path == null) return;
        if (path.size() < 2) return;

        if (mob.blockPosition().equals(walkingTo.getPos())) {
            path.remove(0);
            walkingTo = path.get(0);

            walkingTo.getMove().type.move(mob, walkingTo.getPos());
        }
    }
}
