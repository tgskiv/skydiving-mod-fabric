package com.tgskiv.skydiving.flight;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.RaycastContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FlightUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger("SkydivingMod");
    static int ticks = 0;


    public static double heightCompensatedWindSpeed = 0;
    public static double spinFallDownwardBoost = 0;
    public static double angularSpeed = 0;
    public static double updraftStrength = 0;

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

        heightCompensatedWindSpeed = windSpeed;
        int blocksBelow = FlightUtils.getBlocksBelowPlayer(player);

        if (ticks % 60 == 0) {
            LOGGER.info("Blocks below: {}", blocksBelow);
            com.tgskiv.SkydivingMod.LOGGER.info("Blocks below: {}", blocksBelow);
        }

        ticks++;

        if (blocksBelow <=5) {
            heightCompensatedWindSpeed = heightCompensatedWindSpeed*0.3;
        } else if (blocksBelow <=10) {
            heightCompensatedWindSpeed = heightCompensatedWindSpeed*0.6;
        }

        Vec3d push = windDirection.multiply(heightCompensatedWindSpeed);

        player.addVelocity(push);
    }


    public static void getSpinFallEffect(ClientPlayerEntity player) {
        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();

        float yawDelta = Math.abs(currentYaw - lastYaw);
        float pitchDelta = Math.abs(currentPitch - lastPitch);

        lastYaw = currentYaw;
        lastPitch = currentPitch;

        angularSpeed = Math.sqrt(yawDelta * yawDelta + pitchDelta * pitchDelta);

        // You can tune this threshold and scale factor
        double threshold = 10.0;
        if (angularSpeed > threshold) {
            LOGGER.warn("angularSpeed {}", angularSpeed);
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(String.format("angularSpeed %.2f", angularSpeed)));

            spinFallDownwardBoost = (angularSpeed - threshold) * 0.01;

        } else {
            spinFallDownwardBoost = 0;
        }
    }

    public static void applySpinFallEffect(ClientPlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x, velocity.y - spinFallDownwardBoost, velocity.z);
    }


    /**
     * Calculates the updraft effect
     *
     * @param player ClientPlayerEntity
     * @param windDirection Vec3d
     */
    public static void getUpdraftEffect(ClientPlayerEntity player, Vec3d windDirection) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        BlockPos origin = player.getBlockPos();
        float[][] heights = TerrainAirflowUtils.sampleHeightsAround(TerrainAirflowUtils.size, origin, world);
        float dot = TerrainAirflowUtils.getSlopeWindDot2(heights, windDirection);

        // Detect terrain slope strength: difference between highest and lowest point
        float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
        for (float[] row : heights) {
            for (float h : row) {
                min = Math.min(min, h);
                max = Math.max(max, h);
            }
        }
        float slopeStrength = max - min;

        // Don't apply effect for nearly flat terrain
        if (slopeStrength < 2.5f) return;

        // Determine altitude factor: peak at 10 blocks, fades out by 25 blocks
        int blocksBelow = getBlocksBelowPlayer(player);
        if (blocksBelow < 0 || blocksBelow > 25) return;

        float altitudeFactor = 0f;
        if (blocksBelow <= 10) {
            altitudeFactor = 1f;
        } else if (blocksBelow <= 25) {
            altitudeFactor = 1f - ((blocksBelow - 10) / 15f);
        }

        // Final vertical effect multiplier
        updraftStrength = dot * altitudeFactor * 0.03; // max boost ~0.03 when aligned
    }

    /**
     * Applies updraft effect
     * @param player ClientPlayerEntity
     */
    public static void applyUpdraftEffect(ClientPlayerEntity player) {

        if (!player.isFallFlying()) return;
        if (Math.abs(updraftStrength) < 0.001) return;

        // Apply vertical velocity
        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x, velocity.y + updraftStrength, velocity.z);
    }



}
