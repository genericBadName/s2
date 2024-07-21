package com.genericbadname.s2lib.network.packet;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.client.render.DebugRenderingCache;
import com.genericbadname.s2lib.pathing.BetterBlockPos;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class RenderNodeUpdateS2CPacket {
    public static void runClient(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        BetterBlockPos blockPos;
        boolean valid;

        try {
            blockPos = BetterBlockPos.from(buf.readBlockPos());
            valid = buf.readBoolean();
        } catch(Exception e) {
            S2Lib.LOGGER.warn("Could not read node update packet");
            return;
        }

        //S2Lib.logInfo("Recieved path packet for position {}", blockPos);

        DebugRenderingCache.putBlock(blockPos, valid);
    }

    public static FriendlyByteBuf create(BetterBlockPos blockPos, boolean valid) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(blockPos);
        buf.writeBoolean(valid);

        return buf;
    }
}
