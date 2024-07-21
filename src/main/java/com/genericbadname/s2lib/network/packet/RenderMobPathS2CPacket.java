package com.genericbadname.s2lib.network.packet;

import com.genericbadname.s2lib.S2Lib;
import com.genericbadname.s2lib.client.render.DebugRenderingCache;
import com.genericbadname.s2lib.pathing.S2Path;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class RenderMobPathS2CPacket{
    public static void runClient(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        UUID uuid;
        S2Path path;

        try {
            uuid = buf.readUUID();
            path = S2Path.deserialize(buf);
        } catch(Exception e) {
            S2Lib.LOGGER.warn("Could not read path render packet");
            return;
        }

        //S2Lib.logInfo("Recieved path packet for UUID {}", uuid);

        DebugRenderingCache.putPath(uuid, path);
    }
}
