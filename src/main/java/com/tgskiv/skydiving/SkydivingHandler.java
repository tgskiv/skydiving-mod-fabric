package com.tgskiv.skydiving;

import com.tgskiv.skydiving.configuration.SkydivingServerConfig;
import com.tgskiv.skydiving.configuration.StateSaverAndLoader;
import com.tgskiv.skydiving.network.WindConfigSyncPayload;
import com.tgskiv.skydiving.network.WindSyncPayload;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.slf4j.LoggerFactory;

import static com.tgskiv.skydiving.WindUtils.*;

public class SkydivingHandler {

    private static int ticksUntilWindChange = 0;

    private static StateSaverAndLoader state;
    private static final WindForecast windForecast = new WindForecast(state.skydivingConfig);




    public static void register() {

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            state = StateSaverAndLoader.getServerState(server);
        });

        ServerTickEvents.END_WORLD_TICK.register(SkydivingHandler::onWorldTick);
        CommandRegistrationCallback.EVENT.register(SkydivingHandler::registerCommands);

        PayloadTypeRegistry.playC2S().register(WindConfigSyncPayload.ID, WindConfigSyncPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(WindConfigSyncPayload.ID, (payload, context) -> {
            LoggerFactory.getLogger("SkydivingMod").info("Received WindConfSyncPayload");

            state.updateSettingsWithPayload(payload);
            windForecast.repopulateForecast();
            ticksUntilWindChange = 0;

            context.player().sendMessage(Text.of("§aSettings saved. Wind forecast regenerated."));
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
            ticksUntilWindChange = SkydivingServerConfig.config.ticksPerWindChange;
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
