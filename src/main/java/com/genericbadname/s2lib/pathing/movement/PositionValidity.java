package com.genericbadname.s2lib.pathing.movement;

public enum PositionValidity {
    SUCCESS, // success, all conditions met
    FAIL_BLOCKED, // failure, a block is in the way
    FAIL_MISSING_BLOCK, // failure, a block is missing
    FAIL_UNKNOWN // failure, for any other reason
}
