package com.genericbadname.s2lib.pathing.movement;

import com.genericbadname.s2lib.bakery.eval.BakedLevelAccessor;
import com.genericbadname.s2lib.config.CommonConfig;
import com.genericbadname.s2lib.network.S2NetworkingConstants;
import com.genericbadname.s2lib.network.S2NetworkingUtil;
import com.genericbadname.s2lib.network.packet.RenderNodeUpdateS2CPacket;
import com.genericbadname.s2lib.pathing.BetterBlockPos;

public enum PositionValidity {
    SUCCESS, // success, all conditions met
    FAIL_BLOCKED, // failure, a block is in the way
    FAIL_MISSING_BLOCK, // failure, a block is missing
    FAIL_UNKNOWN; // failure, for any other reason

    public static class PositionValidator {
        public static PositionValidity cardinal(BakedLevelAccessor bakery, BetterBlockPos pos) {
            if (!bakery.isPassable(pos) || !bakery.isPassable(pos.above())) {
                maybeSendUpdate(pos, false, bakery);
                return PositionValidity.FAIL_BLOCKED; // ensure foot is passable
            }

            if (!bakery.isWalkable(pos.below())) {
                maybeSendUpdate(pos.below(), false, bakery);
                return PositionValidity.FAIL_MISSING_BLOCK; // ensure stepping on block is possible
            }

            maybeSendUpdate(pos, true, bakery);
            return PositionValidity.SUCCESS;
        }

        public static PositionValidity northeast(BakedLevelAccessor bakery, BetterBlockPos pos) {
            if (!bakery.isWalkable(pos.offset(0, -1, 0))) {
                maybeSendUpdate(pos, false, bakery);
                return PositionValidity.FAIL_MISSING_BLOCK; // ensure ground is walkable
            }

            if (
                    !bakery.isPassable(pos.offset(0, 0, 0)) ||
                            !bakery.isPassable(pos.offset(0, 1, 0)) ||
                            !bakery.isPassable(pos.offset(-1, 0, 0)) ||
                            !bakery.isPassable(pos.offset(-1, 1, 0)) ||
                            !bakery.isPassable(pos.offset(0, 0, 1)) ||
                            !bakery.isPassable(pos.offset(0, 1, 1))
            ) {
                maybeSendUpdate(pos.above(), false, bakery);
                return PositionValidity.FAIL_BLOCKED; // ensure head and surrounding area are passable
            }

            maybeSendUpdate(pos, true, bakery);
            return PositionValidity.SUCCESS;
        }

        public static PositionValidity northwest(BakedLevelAccessor bakery, BetterBlockPos pos) {
            if (!bakery.isWalkable(pos.offset(0, -1, 0))) {
                maybeSendUpdate(pos, false, bakery);
                return PositionValidity.FAIL_MISSING_BLOCK; // ensure ground is walkable
            }

            if (
                    !bakery.isPassable(pos.offset(0, 0, 0)) ||
                            !bakery.isPassable(pos.offset(0, 1, 0)) ||
                            !bakery.isPassable(pos.offset(-1, 0, 0)) ||
                            !bakery.isPassable(pos.offset(-1, 1, 0)) ||
                            !bakery.isPassable(pos.offset(0, 0, -1)) ||
                            !bakery.isPassable(pos.offset(0, 1, -1))
            ) {
                maybeSendUpdate(pos.above(), false, bakery);
                return PositionValidity.FAIL_BLOCKED; // ensure head and surrounding area are passable
            }

            maybeSendUpdate(pos, true, bakery);
            return PositionValidity.SUCCESS;
        }

        public static PositionValidity southeast(BakedLevelAccessor bakery, BetterBlockPos pos) {
            if (!bakery.isWalkable(pos.offset(0, -1, 0))) {
                maybeSendUpdate(pos, false, bakery);
                return PositionValidity.FAIL_MISSING_BLOCK; // ensure ground is walkable
            }

            if (
                    !bakery.isPassable(pos.offset(0, 0, 0)) ||
                            !bakery.isPassable(pos.offset(0, 1, 0)) ||
                            !bakery.isPassable(pos.offset(1, 0, 0)) ||
                            !bakery.isPassable(pos.offset(1, 1, 0)) ||
                            !bakery.isPassable(pos.offset(0, 0, 1)) ||
                            !bakery.isPassable(pos.offset(0, 1, 1))
            ) {
                maybeSendUpdate(pos.above(), false, bakery);
                return PositionValidity.FAIL_BLOCKED; // ensure head and surrounding area are passable
            }

            maybeSendUpdate(pos, true, bakery);
            return PositionValidity.SUCCESS;
        }

        public static PositionValidity southwest(BakedLevelAccessor bakery, BetterBlockPos pos) {
            if (!bakery.isWalkable(pos.offset(0, -1, 0))) {
                maybeSendUpdate(pos, false, bakery);
                return PositionValidity.FAIL_MISSING_BLOCK; // ensure ground is walkable
            }

            if (
                    !bakery.isPassable(pos.offset(0, 0, 0)) ||
                            !bakery.isPassable(pos.offset(0, 1, 0)) ||
                            !bakery.isPassable(pos.offset(-1, 0, 0)) ||
                            !bakery.isPassable(pos.offset(-1, 1, 0)) ||
                            !bakery.isPassable(pos.offset(0, 0, 1)) ||
                            !bakery.isPassable(pos.offset(0, 1, 1))
            ) {
                maybeSendUpdate(pos.above(), false, bakery);
                return PositionValidity.FAIL_BLOCKED; // ensure head and surrounding area are passable
            }

            maybeSendUpdate(pos, true, bakery);
            return PositionValidity.SUCCESS;
        }

        protected static void maybeSendUpdate(BetterBlockPos pos, boolean valid, BakedLevelAccessor bakery) {
            if (CommonConfig.DEBUG_PATH_CALCULATIONS.get()) S2NetworkingUtil.dispatchAll(S2NetworkingConstants.RENDER_NODE_UPDATE, RenderNodeUpdateS2CPacket.create(pos, valid), bakery.getServer());
        }
    }
}
