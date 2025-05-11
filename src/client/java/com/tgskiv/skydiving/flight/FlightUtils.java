package com.tgskiv.skydiving.flight;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.RaycastContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FlightUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger("SkydivingMod");
    static int ticks = 0;

    private static float lastYaw = 0f;
    private static float lastPitch = 0f;

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




    public static void applySpinFallEffect(ClientPlayerEntity player) {
        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();

        float yawDelta = Math.abs(currentYaw - lastYaw);
        float pitchDelta = Math.abs(currentPitch - lastPitch);

        lastYaw = currentYaw;
        lastPitch = currentPitch;

        double angularSpeed = Math.sqrt(yawDelta * yawDelta + pitchDelta * pitchDelta);

        // You can tune this threshold and scale factor
        double threshold = 10.0;
        if (angularSpeed > threshold) {
            LOGGER.warn("angularSpeed {}", angularSpeed);
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(String.format("angularSpeed %.2f", angularSpeed)));

            double downwardBoost = (angularSpeed - threshold) * 0.01;
            Vec3d velocity = player.getVelocity();
            player.setVelocity(velocity.x, velocity.y - downwardBoost, velocity.z);
        }
    }


}
