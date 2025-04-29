package com.example.skydiving;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.Queue;

import static com.example.skydiving.SkydivingConfig.FORECAST_DISPLAY_COUNT;
import static com.example.skydiving.WindUtils.vectorToCompass;

public class WindForecast {

    private Vec3d currentWindDirection;
    private double currentWindSpeed;

    WindForecast(Vec3d currentWindDirection, double currentWindSpeed) {

    }

    private final Queue<WindChange> forecast = new LinkedList<>();





    public void showForecast(PlayerEntity player) {
        int minutesPerChange = 1;
        int index = 1;
        for (WindChange change : forecast.stream().limit(FORECAST_DISPLAY_COUNT).toList()) {
            String text = String.format("%d min: %s at %.3f", index * minutesPerChange,
                    vectorToCompass(change.direction.x, change.direction.z),
                    change.speed);
            player.sendMessage(Text.literal(text), false);
            index++;
        }
    }

    public Queue<WindChange> getForecast() {
        return forecast;
    }
}
