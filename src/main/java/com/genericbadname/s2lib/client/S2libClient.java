package com.genericbadname.s2lib.client;

import com.genericbadname.s2lib.example.entity.EntityRegistry;
import com.genericbadname.s2lib.example.entity.ExampleS2Renderer;
import com.genericbadname.s2lib.network.S2NetworkingConstants;
import com.genericbadname.s2lib.network.packet.RemoveMobPathS2CPacket;
import com.genericbadname.s2lib.network.packet.RenderMobPathS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class S2libClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityRegistry.EXAMPLE, ExampleS2Renderer::new);

        ClientPlayNetworking.registerGlobalReceiver(S2NetworkingConstants.RENDER_MOB_PATH, RenderMobPathS2CPacket::runClient);
        ClientPlayNetworking.registerGlobalReceiver(S2NetworkingConstants.REMOVE_MOB_PATH, RemoveMobPathS2CPacket::runClient);
    }
}
