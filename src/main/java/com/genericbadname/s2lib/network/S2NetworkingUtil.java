package com.genericbadname.s2lib.network;

import com.genericbadname.s2lib.config.CommonConfig;
import com.genericbadname.s2lib.pathing.entity.S2Mob;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class S2NetworkingUtil {
    public static void dispatchTrackingEntity(ResourceLocation packet, FriendlyByteBuf payload, S2Mob mob) {
        if (!CommonConfig.DEBUG_PATH_CALCULATIONS.get()) return;

        for (ServerPlayer player : PlayerLookup.tracking(mob)) {
            ServerPlayNetworking.send(player, packet, payload);
        }
    }

    public static void dispatchAll(ResourceLocation packet, FriendlyByteBuf payload, MinecraftServer server) {
        if (!CommonConfig.DEBUG_PATH_CALCULATIONS.get()) return;

        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, packet, payload);
        }
    }
}
