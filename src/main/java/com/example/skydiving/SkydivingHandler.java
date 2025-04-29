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
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.Random;

import static com.example.skydiving.SkydivingConfig.*;

import static com.example.skydiving.WindUtils.clampSpeed;
import static com.example.skydiving.WindUtils.vectorToCompass;

public class SkydivingHandler {

    private static int ticksUntilWindChange = 0;
    private static final Random random = new Random();

    private static Vec3d currentWindDirection = new Vec3d(1, 0, 0); // initial east
    private static double currentWindSpeed = 0.01;



    private static final WindForecast windForecast = new WindForecast(currentWindDirection, currentWindSpeed);

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



    private static void generateNextWindChange() {

        // Rotate WIND_ROTATION_DEGREES° randomly left or right
        double angleDelta = Math.toRadians(WIND_ROTATION_DEGREES);
        if (random.nextBoolean()) {
            angleDelta = -angleDelta;
        }

        double cos = Math.cos(angleDelta);
        double sin = Math.sin(angleDelta);

        WindChange last;

        if (!windForecast.getForecast().isEmpty()) {
            last = ((LinkedList<WindChange>) windForecast.getForecast()).getLast();
        } else {
            last = new WindChange(currentWindDirection, currentWindSpeed);
        }


        double newX = last.direction.x * cos - last.direction.z * sin;
        double newZ = last.direction.x * sin + last.direction.z * cos;
        Vec3d newDirection = new Vec3d(newX, 0, newZ).normalize(); // length becomes 1

        // Speed delta ±0.002
        double newSpeed = last.speed + (random.nextDouble() * MAX_SPEED_DELTA - MAX_SPEED_DELTA*2);
        newSpeed = clampSpeed(newSpeed);

        windForecast.getForecast().add(new WindChange(newDirection, newSpeed));
    }


    private static void onWorldTick(ServerWorld world) {
        if (windForecast.getForecast().size() < FORECAST_MIN_SIZE) {
            generateNextWindChange();
        }

        if (ticksUntilWindChange <= 0) {
            applyNextWindChange(world);
            ticksUntilWindChange = TICKS_PER_WIND_CHANGE; // 60 seconds
        } else {
            ticksUntilWindChange--;
        }
    }


    private static void applyNextWindChange(ServerWorld world) {
        if (windForecast.getForecast().isEmpty()) {
            generateNextWindChange();
        }

        WindChange change = windForecast.getForecast().poll();
        if (change == null) return;

        currentWindDirection = change.direction;
        currentWindSpeed = change.speed;

        // Send updated wind to all players
        WindSyncPacket packet = new WindSyncPacket(currentWindDirection, currentWindSpeed);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.write(buf);

        world.getPlayers().forEach(player -> {
            ServerPlayNetworking.send(player, WindSyncPacket.WIND_PACKET_ID, buf);
            player.sendMessage(Text.literal("§bWind updated: " + windToString()), false);
        });
    }


    private static String windToString() {
        String direction = vectorToCompass(currentWindDirection.x, currentWindDirection.z);
        return String.format("Speed: %.2f | Direction: %s", currentWindSpeed, direction);
    }


}
