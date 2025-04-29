package com.example.skydiving;

import com.example.skydiving.network.WindSyncPacket;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import static com.example.skydiving.SkydivingConfig.*;
import static com.example.skydiving.WindUtils.*;

public class SkydivingHandler {

    private static int ticksUntilWindChange = 0;


    private static final WindForecast windForecast = new WindForecast();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(SkydivingHandler::onWorldTick);
        CommandRegistrationCallback.EVENT.register(SkydivingHandler::registerCommands);
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
        );
    }


    private static void onWorldTick(ServerWorld world) {
        windForecast.generateForecast();

        if (ticksUntilWindChange <= 0) {
            applyNextWindChange(world);
            ticksUntilWindChange = TICKS_PER_WIND_CHANGE; // 60 seconds
        } else {
            ticksUntilWindChange--;
        }
    }


    private static void applyNextWindChange(ServerWorld world) {

        WindChange change = windForecast.poll();
        if (change == null) return;

        // Send updated wind to all players
        WindSyncPacket packet = new WindSyncPacket(change.direction, change.speed);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.write(buf);

        world.getPlayers().forEach(player -> {
            ServerPlayNetworking.send(player, WindSyncPacket.WIND_PACKET_ID, buf);
            player.sendMessage(Text.literal("§bWind updated: " + windToString(change.direction, change.speed)), false);
        });
    }


}
