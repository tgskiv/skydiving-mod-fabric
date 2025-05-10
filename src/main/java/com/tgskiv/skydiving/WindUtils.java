package com.tgskiv.skydiving;

import com.tgskiv.skydiving.configuration.SkydivingServerConfig;
import net.minecraft.util.math.Vec3d;

public class WindUtils {
    public static double clampSpeed(double speed) {
        return Math.max(SkydivingServerConfig.config.minWindSpeed, Math.min(SkydivingServerConfig.config.maxWindSpeed, speed));
    }

    public static String vectorToCompass(double x, double z) {
        double angle = Math.toDegrees(Math.atan2(-x, z));
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


    public static String windToString(Vec3d direction, double speed) {
        String strDirection = vectorToCompass(direction.x, direction.z);

        // let's say 0.02 is 10m/s
        return String.format("Speed: %.2fm/s | Direction: %s", speed*200, strDirection);
    }

}
