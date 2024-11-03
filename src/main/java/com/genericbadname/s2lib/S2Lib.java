package com.genericbadname.s2lib;

import com.genericbadname.s2lib.bakery.storage.BakeryAttachment;
import com.genericbadname.s2lib.command.PathfindingTestCommand;
import com.genericbadname.s2lib.command.BakeryCommand;
import com.genericbadname.s2lib.config.CommonConfig;
import com.genericbadname.s2lib.config.ServerConfig;
import com.genericbadname.s2lib.example.entity.EntityRegistry;
import com.genericbadname.s2lib.example.entity.ExampleS2Entity;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class S2Lib implements ModInitializer {
    public static final String MOD_ID = "s2lib";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // register config
        ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.SERVER, ServerConfig.SPEC);
        ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, CommonConfig.SPEC);

        // register debug commands
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            PathfindingTestCommand.register(dispatcher, false);
        }));

        // register debug entity
        FabricDefaultAttributeRegistry.register(EntityRegistry.EXAMPLE, ExampleS2Entity.createAttributes().build());

        // register commands
        CommandRegistrationCallback.EVENT.register((((dispatcher, registryAccess, environment) -> {
            BakeryCommand.register(dispatcher);
        })));

        // bakery lifecycle
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            server.initBakeries();
            server.loadBakeries();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(BakeryAttachment::writeBakeries);
        ServerLifecycleEvents.SERVER_STOPPED.register(BakeryAttachment::clearBakeries);
    }

    public static ResourceLocation asResource(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static void logInfo(String message) {
        if (!CommonConfig.DEBUG_LOGGING.get()) return;

        LOGGER.info(message);
    }

    public static void logInfo(String message, Object... p0) {
        if (!CommonConfig.DEBUG_LOGGING.get()) return;

        LOGGER.info(message, p0);
    }
}
