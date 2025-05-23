// src/main/java/com/example/skydiving/registry/ModBlockEntities.java
package com.tgskiv.skydiving.registry;

import com.tgskiv.SkydivingMod; // Your main mod class
import com.tgskiv.skydiving.blockentity.WindsockBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<WindsockBlockEntity> WINDSOCK_BLOCK_ENTITY;

    // Called from your main mod initializer
    public static void registerBlockEntities() {
        WINDSOCK_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SkydivingMod.MOD_ID, "windsock_block_entity"),
                FabricBlockEntityTypeBuilder.create(WindsockBlockEntity::new, ModBlocks.WINDSOCK).build()
        );

        SkydivingMod.LOGGER.info("Registering ModBlockEntities for " + SkydivingMod.MOD_ID);
    }

}