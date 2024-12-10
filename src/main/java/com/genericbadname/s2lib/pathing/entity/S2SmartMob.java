package com.genericbadname.s2lib.pathing.entity;

import com.genericbadname.s2lib.pathing.BetterBlockPos;
import com.genericbadname.s2lib.pathing.S2Path;

import java.io.IOException;

public interface S2SmartMob {
    void updatePath();
    S2Path getPotentialPath();
    S2Path calculateFromCurrentLocation(BetterBlockPos dest) throws IOException;
}
