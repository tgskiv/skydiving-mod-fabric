package com.tgskiv.skydiving.registry;


import com.tgskiv.SkydivingMod;
import com.tgskiv.skydiving.block.WindsockBlock;
import net.minecraft.block.AbstractBlock;
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
            new WindsockBlock(AbstractBlock.Settings
                    .create()
                    .solid()
                    .strength(2.0f) // Adjust hardness
                    .sounds(BlockSoundGroup.WOOD) // Adjust sounds
                    .nonOpaque() // Important for BERs so light passes through the model correctly
    ));


    public static final Item WINDSOCK_ITEM = registerBlockItem("windsock",
            new BlockItem(WINDSOCK, new Item.Settings())
    );



    // Helper method to register block and associated item
    private static Block registerBlock(String name, Block block) {
        return Registry.register(
                Registries.BLOCK,
                Identifier.of(SkydivingMod.MOD_ID, name),
                block
        );
    }

    private static Item registerBlockItem(String name, Item item) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of(SkydivingMod.MOD_ID, name),
                item
        );
    }

    // Called from your main mod initializer
    public static void registerModBlocks() {
        SkydivingMod.LOGGER.info("Registering ModBlocks for " + SkydivingMod.MOD_ID);

    }
}