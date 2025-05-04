package com.example.skydiving;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static com.example.skydiving.SkydivingConfig.*;
import static com.example.skydiving.SkydivingConfig.MAX_SPEED_DELTA;
import static com.example.skydiving.WindUtils.*;

public class WindForecast {

    private static final Random random = new Random();


    private final Queue<WindChange> forecast = new LinkedList<>();


    public void generateForecast() {
        while (forecast.size() < SkydivingConfig.FORECAST_MIN_SIZE) {
            generateNextWindChange();
        }
    }

    public WindChange poll() {
        return forecast.poll();
    }

    private void generateNextWindChange() {

        // Rotate WIND_ROTATION_DEGREES° randomly left or right
        double angleDelta = Math.toRadians(WIND_ROTATION_DEGREES);
        if (random.nextBoolean()) {
            angleDelta = -angleDelta;
        }

        double cos = Math.cos(angleDelta);
        double sin = Math.sin(angleDelta);

        WindChange latestWind;

        if (!forecast.isEmpty()) { // that's the first generation
            latestWind = ((LinkedList<WindChange>) forecast).getLast();
        } else {
            latestWind = new WindChange(new Vec3d(1, 0, 0), 0.01);
        }


        double newX = latestWind.direction.x * cos - latestWind.direction.z * sin;
        double newZ = latestWind.direction.x * sin + latestWind.direction.z * cos;
        Vec3d newDirection = new Vec3d(newX, 0, newZ).normalize(); // length becomes 1

        double newSpeed = latestWind.speed + (random.nextBoolean() ? MAX_SPEED_DELTA : -MAX_SPEED_DELTA);

        newSpeed = clampSpeed(newSpeed);

        forecast.add(new WindChange(newDirection, newSpeed));
    }


    public void showForecast(PlayerEntity player) {
        int minutesPerChange = 1;
        int index = 1;
        for (WindChange change : forecast.stream().limit(FORECAST_DISPLAY_COUNT).toList()) {
            String text = String.format("%d min: %s", index * minutesPerChange,
                    windToString(change.direction, change.speed));

            player.sendMessage(Text.literal(text), false);
            index++;
        }
    }

}
