package com.example.skydiving;

import com.example.skydiving.network.WindSyncPacket;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SkydivingHandler {

    private static int ticksUntilWindChange = 0;
    private static final Random random = new Random();

    private static Vec3d currentWindDirection = new Vec3d(1, 0, 0); // initial east
    private static double currentWindSpeed = 0.005;

    public static final Identifier WIND_PACKET_ID = new Identifier("skydivingmod", "wind_sync");

    private static final Queue<WindChange> forecast = new LinkedList<>();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(SkydivingHandler::onWorldTick);
        CommandRegistrationCallback.EVENT.register(SkydivingHandler::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("wind")
                .then(CommandManager.literal("forecast")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            showForecast(source.getPlayer());
                            return 1;
                        })
                )
        );
    }

    private static void showForecast(PlayerEntity player) {
        int minutesPerChange = 1;
        int index = 1;
        for (WindChange change : forecast.stream().limit(5).toList()) {
            String text = String.format("%d min: %s at %.3f", index * minutesPerChange,
                    vectorToCompass(change.direction.x, change.direction.z),
                    change.speed);
            player.sendMessage(Text.literal(text), false);
            index++;
        }
    }

    private static class WindChange {
        final Vec3d direction;
        final double speed;

        WindChange(Vec3d direction, double speed) {
            this.direction = direction;
            this.speed = speed;
        }
    }



    private static void generateNextWindChange() {


        // Rotate 5° randomly left or right
        double angleDelta = Math.toRadians(15.0);
        if (random.nextBoolean()) {
            angleDelta = -angleDelta;
        }

        double cos = Math.cos(angleDelta);
        double sin = Math.sin(angleDelta);

        WindChange last;

        if (!forecast.isEmpty()) {
            last = ((LinkedList<WindChange>) forecast).getLast();
        } else {
            last = new WindChange(currentWindDirection, currentWindSpeed);
        }


        double newX = last.direction.x * cos - last.direction.z * sin;
        double newZ = last.direction.x * sin + last.direction.z * cos;
        Vec3d newDirection = new Vec3d(newX, 0, newZ).normalize();

        // Speed delta ±0.002
        double newSpeed = last.speed + (random.nextDouble() * 0.004 - 0.002);
        newSpeed = Math.max(0.0, Math.min(0.02, newSpeed)); // Clamp between 0 and 0.02

        forecast.add(new WindChange(newDirection, newSpeed));
    }


    private static void onWorldTick(ServerWorld world) {
        if (forecast.size() < 5) {
            generateNextWindChange();
        }

        if (ticksUntilWindChange <= 0) {
            applyNextWindChange(world);
            ticksUntilWindChange = 1200; // 60 seconds
        } else {
            ticksUntilWindChange--;
        }

        /*
        // Wind changes every 600 ticks (~30 seconds)
        if (ticksUntilWindChange <= 0) {
            double angleDelta = Math.toRadians(random.nextDouble() * 90.0); // 0–90° in radians
            boolean clockwise = random.nextBoolean();
            if (!clockwise) angleDelta = -angleDelta;

            double cos = Math.cos(angleDelta);
            double sin = Math.sin(angleDelta);

            // Rotate X/Z using basic 2D rotation matrix
            double newX = currentWindDirection.x * cos - currentWindDirection.z * sin;
            double newZ = currentWindDirection.x * sin + currentWindDirection.z * cos;

            currentWindDirection = new Vec3d(newX, 0, newZ).normalize();
            currentWindSpeed = (random.nextDouble()) * 0.03; // wind between 0.5 and 2.0

            WindSyncPacket packet = new WindSyncPacket(currentWindDirection, currentWindSpeed);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);

            world.getPlayers().forEach(player -> {
                ServerPlayNetworking.send(player, WIND_PACKET_ID, buf);
                player.sendMessage(Text.literal("§bWind changed: " + windToString()), false);
            });

            ticksUntilWindChange = 600;
        } else {
            ticksUntilWindChange--;
        }

        world.getPlayers().forEach(player -> {
            if (!player.isFallFlying()) {
                return;
            }

            if (ticksUntilWindChange % 30 == 0) {
                Vec3d velocity = player.getVelocity();
                player.sendMessage(Text.literal("§bGround Speed: " + String.format("%.2f", Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z))), false);
            }
        });
        */
    }



    private static void applyNextWindChange(ServerWorld world) {
        if (forecast.isEmpty()) {
            generateNextWindChange();
        }

        WindChange change = forecast.poll();
        if (change == null) return;

        currentWindDirection = change.direction;
        currentWindSpeed = change.speed;

        // Send updated wind to all players
        WindSyncPacket packet = new WindSyncPacket(currentWindDirection, currentWindSpeed);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.write(buf);

        world.getPlayers().forEach(player -> {
            ServerPlayNetworking.send(player, WIND_PACKET_ID, buf);
            player.sendMessage(Text.literal("§bWind updated: " + windToString()), false);
        });
    }


    private static String windToString() {
        String direction = vectorToCompass(currentWindDirection.x, currentWindDirection.z);
        return String.format("Speed: %.2f | Direction: %s", currentWindSpeed, direction);
    }

    private static String vectorToCompass(double x, double z) {
        double angle = Math.toDegrees(Math.atan2(-x, z)); // North is 0°, clockwise
        angle = (angle + 360) % 360;

        String[] directions = {
                "N", "NNE", "NE", "ENE",
                "E", "ESE", "SE", "SSE",
                "S", "SSW", "SW", "WSW",
                "W", "WNW", "NW", "NNW"
        };

        int index = (int) Math.round(angle / 22.5) % 16;
        return directions[index];
    }

}
