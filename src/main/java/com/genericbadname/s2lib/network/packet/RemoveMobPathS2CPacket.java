package com.genericbadname.s2lib.network.packet;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.client.render.DebugRenderingCache;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class RemoveMobPathS2CPacket {
    public static void runClient(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        UUID uuid;

        try {
            uuid = buf.readUUID();
        } catch(Exception e) {
            S2Lib.LOGGER.warn("Could not read path removal packet");
            return;
        }

        //S2Lib.logInfo("Recieved path removal packet for UUID {}", uuid);

        DebugRenderingCache.removePath(uuid);
    }
}
