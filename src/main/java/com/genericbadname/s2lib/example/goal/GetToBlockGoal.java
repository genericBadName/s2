package com.genericbadname.s2lib.example.goal;

import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Node;
import com.genericbadname.s2lib.pathing.S2Path;
import com.genericbadname.s2lib.pathing.entity.S2Mob;
import com.genericbadname.s2lib.pathing.movement.Moves;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class GetToBlockGoal extends Goal {
    private final S2Mob mob;
    private final S2Node target;
    private List<S2Node> path;
    private S2Node walkingTo;

    public GetToBlockGoal(S2Mob mob, BetterBlockPos target) {
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
        Optional<S2Path> potentialPath = mob.calculateFromCurrentLocation(target.getPos());
        if (potentialPath.isEmpty()) return;

        this.path = potentialPath.get().getNodes();
        walkingTo = path.get(0);
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
