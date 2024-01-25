package com.genericbadname.s2lib.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IEntityRenderManager {

    double renderPosX();

    double renderPosY();

    double renderPosZ();
}
