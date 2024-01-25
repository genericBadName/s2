package com.genericbadname.s2lib.client.render;

import com.genericbadname.s2lib.pathing.S2Node;
import com.genericbadname.s2lib.pathing.S2Path;
import com.genericbadname.s2lib.pathing.movement.IMovement;
import com.genericbadname.s2lib.pathing.movement.Moves;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class PathRenderer implements IRenderer {
    private PathRenderer() {}

    public static double posX() {
        return renderManager.renderPosX();
    }

    public static double posY() {
        return renderManager.renderPosY();
    }

    public static double posZ() {
        return renderManager.renderPosZ();
    }

    public static void renderPath(PoseStack stack, S2Path path, float offset) {
        List<S2Node> nodes = path.getNodes();
        Iterator<S2Node> iterator = nodes.iterator();

        IRenderer.startLines(Color.RED, 5, true);
        S2Node current = null;

        while (iterator.hasNext()) {
            if (current == null) current = iterator.next();

            S2Node next = iterator.next();
            BlockPos pos1 = current.getPos();
            BlockPos pos2 = next.getPos();

            IRenderer.glColor(movementToColor(current.getMove()),0.4f);
            emitPathLine(stack, pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ(), offset);
            current = next;
        }

        IRenderer.endLines(true);
    }

    private static void emitPathLine(PoseStack stack, double x1, double y1, double z1, double x2, double y2, double z2, double offset) {
        final double extraOffset = offset + 0.03D;

        double vpX = posX();
        double vpY = posY();
        double vpZ = posZ();
        boolean renderPathAsFrickinThingy = false;

        IRenderer.emitLine(stack,
                x1 + offset - vpX, y1 + offset - vpY, z1 + offset - vpZ,
                x2 + offset - vpX, y2 + offset - vpY, z2 + offset - vpZ
        );
        if (renderPathAsFrickinThingy) {
            IRenderer.emitLine(stack,
                    x2 + offset - vpX, y2 + offset - vpY, z2 + offset - vpZ,
                    x2 + offset - vpX, y2 + extraOffset - vpY, z2 + offset - vpZ
            );
            IRenderer.emitLine(stack,
                    x2 + offset - vpX, y2 + extraOffset - vpY, z2 + offset - vpZ,
                    x1 + offset - vpX, y1 + extraOffset - vpY, z1 + offset - vpZ
            );
            IRenderer.emitLine(stack,
                    x1 + offset - vpX, y1 + extraOffset - vpY, z1 + offset - vpZ,
                    x1 + offset - vpX, y1 + offset - vpY, z1 + offset - vpZ
            );
        }
    }

    private static void renderHorizontalQuad(PoseStack stack, double minX, double maxX, double minZ, double maxZ, double y) {
        if (y != 0) {
            IRenderer.emitLine(stack, minX, y, minZ, maxX, y, minZ, 1.0, 0.0, 0.0);
            IRenderer.emitLine(stack, maxX, y, minZ, maxX, y, maxZ, 0.0, 0.0, 1.0);
            IRenderer.emitLine(stack, maxX, y, maxZ, minX, y, maxZ, -1.0, 0.0, 0.0);
            IRenderer.emitLine(stack, minX, y, maxZ, minX, y, minZ, 0.0, 0.0, -1.0);
        }
    }

    private static Color movementToColor(Moves move) {
        return switch(move) {
            case START -> Color.GREEN;
            case WALK_NORTH, WALK_SOUTH, WALK_EAST, WALK_WEST, WALK_NORTHEAST, WALK_NORTHWEST, WALK_SOUTHEAST, WALK_SOUTHWEST -> Color.GRAY;
            case STEP_UP_NORTH, STEP_UP_SOUTH, STEP_UP_EAST, STEP_UP_WEST, STEP_UP_NORTHEAST, STEP_UP_NORTHWEST, STEP_UP_SOUTHEAST, STEP_UP_SOUTHWEST -> Color.LIGHT_GRAY;
            case PARKOUR_NORTH, PARKOUR_SOUTH, PARKOUR_EAST, PARKOUR_WEST, PARKOUR_NORTHEAST, PARKOUR_NORTHWEST, PARKOUR_SOUTHEAST, PARKOUR_SOUTHWEST -> Color.MAGENTA;
            case FALL_NORTH, FALL_SOUTH, FALL_EAST, FALL_WEST, FALL_NORTHEAST, FALL_NORTHWEST, FALL_SOUTHEAST, FALL_SOUTHWEST -> Color.PINK;
        };
    }
}
