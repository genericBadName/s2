package com.genericbadname.s2lib.client;

import com.genericbadname.s2lib.command.ClearDebugRenderCommand;
import com.genericbadname.s2lib.network.S2NetworkingConstants;
import com.genericbadname.s2lib.network.packet.ClearNodesS2CPacket;
import com.genericbadname.s2lib.network.packet.RemoveMobPathS2CPacket;
import com.genericbadname.s2lib.network.packet.RenderMobPathS2CPacket;
import com.genericbadname.s2lib.network.packet.RenderNodeUpdateS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class S2libClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // networking listeners
        ClientPlayNetworking.registerGlobalReceiver(S2NetworkingConstants.RENDER_MOB_PATH, RenderMobPathS2CPacket::runClient);
        ClientPlayNetworking.registerGlobalReceiver(S2NetworkingConstants.REMOVE_MOB_PATH, RemoveMobPathS2CPacket::runClient);
        ClientPlayNetworking.registerGlobalReceiver(S2NetworkingConstants.RENDER_NODE_UPDATE, RenderNodeUpdateS2CPacket::runClient);
        ClientPlayNetworking.registerGlobalReceiver(S2NetworkingConstants.CLEAR_NODES, ClearNodesS2CPacket::runClient);

        // client commands
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            ClearDebugRenderCommand.register(dispatcher);
        }));
    }
}
