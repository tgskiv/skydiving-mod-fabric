package com.tgskiv;

import com.tgskiv.skydiving.SkydivingHandler;
import com.tgskiv.skydiving.network.WindConfigSyncPayload;
import com.tgskiv.skydiving.network.WindSyncPayload;
import com.tgskiv.skydiving.registry.ModBlockEntities;
import com.tgskiv.skydiving.registry.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkydivingMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "skydivingmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world (main)!");

		SkydivingHandler.register();
		PayloadTypeRegistry.playS2C().register(WindSyncPayload.PAYLOAD_ID, WindSyncPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(WindConfigSyncPayload.PAYLOAD_ID, WindConfigSyncPayload.CODEC);


		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
	}
}