package com.genericbadname.s2lib.network.packet;

import com.genericbadname.s2lib.client.render.DebugRenderingCache;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class ClearNodesS2CPacket {
    public static void runClient(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        DebugRenderingCache.clearBlocks();
    }
}
