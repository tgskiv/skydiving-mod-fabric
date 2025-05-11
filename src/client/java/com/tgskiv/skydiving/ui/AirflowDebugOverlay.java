package com.tgskiv.skydiving.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tgskiv.SkydivingModClient;
import com.tgskiv.skydiving.flight.TerrainAirflowUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import java.awt.Color;

public class AirflowDebugOverlay implements HudRenderCallback {

    private static final int CELL_SIZE = 8;
    private static final int GRID_WIDTH = 11;
    private static final int GRID_HEIGHT = 10;
    private static final int PADDING = 10;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
//        if (mc.player == null || mc.world == null || !mc.player.isFallFlying()) return;
        if (mc.player == null || mc.world == null) return;

        TextRenderer textRenderer = mc.textRenderer;
        Vec3d windDir = SkydivingModClient.getWindDirection();
        float dot = TerrainAirflowUtils.getSlopeWindDot(mc.player, windDir, mc.world);

        float[][] heights = sampleHeights(mc.player, mc.world);

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (float[] row : heights) {
            for (float h : row) {
                min = Math.min(min, h);
                max = Math.max(max, h);
            }
        }

        float range = max - min;
        if (range == 0) range = 1f;

        for (int z = 0; z < GRID_HEIGHT; z++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                float norm = (heights[z][x] - min) / range;
                int gray = (int) (norm * 255);
                int drawX = PADDING + x * CELL_SIZE;
                int drawY = PADDING + z * CELL_SIZE;
                int color = 0xFF000000 | (gray << 16) | (gray << 8) | gray;

                drawContext.fill(drawX, drawY, drawX + CELL_SIZE, drawY + CELL_SIZE, color);
            }
        }

        // Calculate wind direction angle
        double angle = Math.atan2(windDir.z, windDir.x); // Z = north/south, X = east/west

        // Draw wind arrow above the grid
        int centerX = PADDING + (GRID_WIDTH * CELL_SIZE) / 2;
        int centerY = PADDING - 2;

        int endX = centerX + (int) (Math.cos(angle) * CELL_SIZE * 4);
        int endY = centerY + (int) (Math.sin(angle) * CELL_SIZE * 4);

        drawArrow(drawContext, centerX, centerY, endX, endY, 0xFF00FF00);

        // Dot product text (existing)
        String dotText = String.format("dot = %.2f", dot);
        int dotTextY = PADDING + GRID_HEIGHT * CELL_SIZE + 5;
        drawContext.drawText(textRenderer, dotText, PADDING, dotTextY, 0xFFFFFF, false);

// Derived updraft/downwash
        double airflow = dot * 0.02; // Example multiplier
        String flowText = String.format("Vertical airflow: %.3f", airflow);
        drawContext.drawText(textRenderer, flowText, PADDING, dotTextY + 10, airflow > 0 ? 0x00FF00 : 0xFF5555, false);



    }

    private String getCompassDirection(int angle) {
        String[] directions = {"E", "NE", "N", "NW", "W", "SW", "S", "SE"};
        return directions[(int) Math.round(((double) angle % 360) / 45.0) % 8];
    }



    private float[][] sampleHeights(ClientPlayerEntity player, World world) {
        float[][] heights = new float[GRID_HEIGHT][GRID_WIDTH];

        Vec2f lookDir = new Vec2f((float) player.getRotationVec(1.0f).x, (float) player.getRotationVec(1.0f).z).normalize();
        BlockPos origin = player.getBlockPos();
        int px = origin.getX();
        int pz = origin.getZ();

        for (int f = 0; f < GRID_HEIGHT; f++) {
            for (int w = -GRID_WIDTH / 2; w <= GRID_WIDTH / 2; w++) {
                float dx = lookDir.x * f - lookDir.y * w;
                float dz = lookDir.y * f + lookDir.x * w;

                int sx = px + Math.round(dx);
                int sz = pz + Math.round(dz);

                int sy = world.getTopY(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, sx, sz);
                heights[f][w + GRID_WIDTH / 2] = sy;
            }
        }
        return heights;
    }

    private void drawLine(DrawContext drawContext, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            drawContext.fill(x1, y1, x1 + 1, y1 + 1, color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx)  { err += dx; y1 += sy; }
        }
    }


    private void drawArrow(DrawContext drawContext, int x1, int y1, int x2, int y2, int color) {
        // Main shaft
        drawLine(drawContext, x1, y1, x2, y2, color);

        // Arrowhead
        double angle = Math.atan2(y2 - y1, x2 - x1);

        double headSize = 5.0;
        double angle1 = angle + Math.toRadians(135);
        double angle2 = angle - Math.toRadians(135);

        int hx1 = x2 + (int) (Math.cos(angle1) * headSize);
        int hy1 = y2 + (int) (Math.sin(angle1) * headSize);

        int hx2 = x2 + (int) (Math.cos(angle2) * headSize);
        int hy2 = y2 + (int) (Math.sin(angle2) * headSize);

        drawLine(drawContext, x2, y2, hx1, hy1, color);
        drawLine(drawContext, x2, y2, hx2, hy2, color);
    }



}
