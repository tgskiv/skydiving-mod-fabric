package com.tgskiv.skydiving.flight;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.RaycastContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tgskiv.SkydivingMod.LOGGER;

public class FlightUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger("SkydivingMod");
    static int ticks = 0;

    public static int getBlocksBelowPlayer(ClientPlayerEntity player) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return -1;

        Vec3d eyePos = player.getEyePos();
        Vec3d down = eyePos.add(0, -256, 0); // Max vertical check

        BlockHitResult result = world.raycast(new RaycastContext(
                eyePos,
                down,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.ANY,
                player
        ));

        if (result.getType() == HitResult.Type.BLOCK) {
            double distance = eyePos.y - result.getPos().y;
            return (int) Math.floor(distance);
        }
        return -1;
    }



    public static void applyWindToPlayer(ClientPlayerEntity player, Vec3d windDirection, double windSpeed) {
        if (windSpeed <= 0) return;


        double adjustedWindSpeed = windSpeed;
        int blocksBelow = FlightUtils.getBlocksBelowPlayer(player);

        if (ticks % 60 == 0) {
            LOGGER.info("Blocks below: {}", blocksBelow);
            com.tgskiv.SkydivingMod.LOGGER.info("Blocks below: {}", blocksBelow);
        }

        ticks++;

        if (blocksBelow <=5) {
            adjustedWindSpeed = adjustedWindSpeed*0.3;
        } else if (blocksBelow <=10) {
            adjustedWindSpeed = adjustedWindSpeed*0.6;
        }


        Vec3d push = windDirection.multiply(adjustedWindSpeed);

        player.addVelocity(push);
    }


}
