package com.example.skydiving;

import com.example.skydiving.network.WindSyncPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class SkydivingHandler {

    private static int ticksUntilWindChange = 0;
    private static final Random random = new Random();

    private static Vec3d currentWindDirection = new Vec3d(1, 0, 0); // initial east
    private static double currentWindSpeed = 0.005;

    public static final Identifier WIND_PACKET_ID = new Identifier("skydivingmod", "wind_sync");

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(SkydivingHandler::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
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
            currentWindSpeed = (random.nextDouble()) * 0.02; // wind between 0.5 and 2.0

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
