package com.tgskiv.skydiving.flight;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TerrainAirflowUtils {


    /**
     * Samples terrain slope in front of the player and compares it with wind direction.
     * @param player The player entity.
     * @param windDirection The current wind direction (should be a Vec3d).
     * @param world The world the player is in.
     * @return dot product between terrain slope direction and wind direction, in range [-1, 1].
     */
    public static float getSlopeWindDot(ClientPlayerEntity player, Vec3d windDirection, World world) {
        final int forwardDistance = 10;
        final int halfWidth = 5;

        // Flatten player's look direction to XZ and normalize
        Vec2f lookDir = new Vec2f((float) player.getRotationVec(1.0f).x, (float) player.getRotationVec(1.0f).z).normalize();

        // Get player's ground position
        BlockPos origin = player.getBlockPos();
        int playerX = origin.getX();
        int playerZ = origin.getZ();

        float[][] heightMap = new float[forwardDistance][2 * halfWidth + 1];

        // Sampling loop
        for (int f = 0; f < forwardDistance; f++) {
            for (int w = -halfWidth; w <= halfWidth; w++) {
                // Direction vector: forward + side
                float dx = lookDir.x * f - lookDir.y * w;
                float dz = lookDir.y * f + lookDir.x * w;

                int sampleX = playerX + Math.round(dx);
                int sampleZ = playerZ + Math.round(dz);

                int sampleY = world.getTopY(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, sampleX, sampleZ);
                heightMap[f][w + halfWidth] = sampleY;
            }
        }

        // Compute approximate gradient using central differences
        float dX = 0f;
        float dZ = 0f;

        for (int f = 1; f < forwardDistance - 1; f++) {
            for (int w = 1; w < 2 * halfWidth; w++) {
                float dzForward = heightMap[f + 1][w] - heightMap[f - 1][w];
                float dxSide = heightMap[f][w + 1] - heightMap[f][w - 1];

                dX += dxSide;
                dZ += dzForward;
            }
        }

        int count = (forwardDistance - 2) * (2 * halfWidth - 1);
        dX /= count;
        dZ /= count;

        Vec2f slopeDir = new Vec2f(dX, dZ).normalize();
        Vec2f windDirXZ = new Vec2f((float) windDirection.x, (float) windDirection.z).normalize();

        return windDirXZ.dot(slopeDir); // result in [-1, 1]
    }

}
