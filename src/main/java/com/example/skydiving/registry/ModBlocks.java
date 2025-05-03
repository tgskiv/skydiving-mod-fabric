package com.example.skydiving.registry;


import com.example.SkydivingMod;
import com.example.skydiving.block.WindsockBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    // Create and register the Windsock block instance
    public static final Block WINDSOCK = registerBlock("windsock",
            new WindsockBlock(FabricBlockSettings
                    .create()
                    .solid()
                    .strength(2.0f) // Adjust hardness
                    .sounds(BlockSoundGroup.WOOD) // Adjust sounds
                    .nonOpaque() // Important for BERs so light passes through the model correctly
            ));

    // Helper method to register block and associated item
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(SkydivingMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(SkydivingMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    // Called from your main mod initializer
    public static void registerModBlocks() {
        SkydivingMod.LOGGER.info("Registering ModBlocks for " + SkydivingMod.MOD_ID);
        // The static initializers above handle the registration
    }
}