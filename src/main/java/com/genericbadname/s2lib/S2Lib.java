package com.genericbadname.s2lib;

import com.genericbadname.s2lib.command.PathfindingTestCommand;
import com.genericbadname.s2lib.example.entity.EntityRegistry;
import com.genericbadname.s2lib.example.entity.ExampleS2Entity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class S2Lib implements ModInitializer {
    public static final String MOD_ID = "s2lib";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            PathfindingTestCommand.register(dispatcher, false);
        }));

        // register
        FabricDefaultAttributeRegistry.register(EntityRegistry.EXAMPLE, ExampleS2Entity.createAttributes().build());
    }

    public static ResourceLocation asResource(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
