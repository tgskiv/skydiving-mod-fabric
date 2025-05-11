package com.tgskiv.skydiving;

import com.tgskiv.skydiving.configuration.StateSaverAndLoader;
import com.tgskiv.skydiving.network.WindConfigSyncPayload;
import com.tgskiv.skydiving.network.WindSyncPayload;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import static com.tgskiv.skydiving.WindUtils.*;

public class SkydivingHandler {

    private static int ticksUntilWindChange = 0;

    private static StateSaverAndLoader state;
    private static WindForecast windForecast;




    public static void register() {

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            state = StateSaverAndLoader.getServerState(server);
            windForecast = new WindForecast(state.skydivingConfig);
        });


        ServerTickEvents.END_WORLD_TICK.register(SkydivingHandler::onWorldTick);
        CommandRegistrationCallback.EVENT.register(SkydivingHandler::registerCommands);

        // Client to Server
        // Sends wind configuration when user saves the settings
        PayloadTypeRegistry.playC2S().register(WindConfigSyncPayload.PAYLOAD_ID, WindConfigSyncPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(WindConfigSyncPayload.PAYLOAD_ID, (payload, context) -> {

            state.updateSettingsWithPayload(payload);
            windForecast.repopulateForecast();
            ticksUntilWindChange = 0;

            context.player().sendMessage(Text.of("§aSettings saved. Wind forecast regenerated."));
        });

        // Load the settings when player joins the server
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            WindConfigSyncPayload payload = new WindConfigSyncPayload(
                    state.skydivingConfig.ticksPerWindChange,
                    state.skydivingConfig.windRotationDegrees,
                    state.skydivingConfig.maxSpeedDelta,
                    state.skydivingConfig.maxWindSpeed,
                    state.skydivingConfig.minWindSpeed
            );
            ServerPlayNetworking.send(handler.player, payload);
        });
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("wind")
                .then(CommandManager.literal("forecast")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            windForecast.showForecast(source.getPlayer());
                            return 1;
                        })
                )
                .then(CommandManager.literal("again")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            windForecast.repopulateForecast();
                            ticksUntilWindChange = 0;
                            source.sendFeedback(() -> Text.literal("§aWind forecast regenerated."), false);
                            return 1;
                        })
                )
        );
    }

    private static void onWorldTick(ServerWorld world) {
        windForecast.populateForecast();

        if (ticksUntilWindChange <= 0) {
            applyNextWindChange(world);
            ticksUntilWindChange = state.skydivingConfig.ticksPerWindChange;
        } else {
            ticksUntilWindChange--;
        }
    }

    private static void applyNextWindChange(ServerWorld world) {
        WindChange change = windForecast.poll();
        if (change == null) return;


        WindSyncPayload payload = new WindSyncPayload(change.direction, change.speed);

        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, payload);
            player.sendMessage(Text.literal("§bWind updated: " + windToString(change.direction, change.speed)), false);
        }
    }
}
