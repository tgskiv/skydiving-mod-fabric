// src/main/java/com/example/skydiving/registry/ModModelLayers.java
package com.example.skydiving.registry;

import com.example.SkydivingMod; // Your main mod class
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {

    public static final EntityModelLayer WINDSOCK_LAYER =
            new EntityModelLayer(new Identifier(SkydivingMod.MOD_ID, "windsock"), "main");

    // No registration method needed here, layers are registered in the client initializer
}
