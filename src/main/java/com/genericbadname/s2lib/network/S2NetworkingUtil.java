package com.genericbadname.s2lib.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

public class S2NetworkingUtil {
    public static void dispatchTrackingEntity(ResourceLocation packet, FriendlyByteBuf payload, Mob mob) {
        for (ServerPlayer player : PlayerLookup.tracking(mob)) {
            ServerPlayNetworking.send(player, packet, payload);
        }
    }

    public static void dispatchAll(ResourceLocation packet, FriendlyByteBuf payload, MinecraftServer server) {
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, packet, payload);
        }
    }
}
