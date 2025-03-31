package com.genericbadname.s2lib.pathing.entity;

import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Path;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface S2SmartMob {
    void s2$updatePath();
    S2Path s2$getPotentialPath();
    CompletableFuture<Void> s2$calculateFromCurrentLocation(BetterBlockPos dest) throws IOException;
}
