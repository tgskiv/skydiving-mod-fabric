package com.tgskiv.skydiving.flight;

import com.tgskiv.skydiving.menu.SkydivingClientConfig;
import com.tgskiv.skydiving.ui.ChatUtils;
import net.minecraft.util.math.Vec3d;

public class WindInterpolator {

    private static Vec3d currentWindDirection = Vec3d.ZERO;
    private static double currentWindSpeed = 0.0;

    private static Vec3d targetWindDirection = Vec3d.ZERO;
    private static double targetWindSpeed = 0.0;

    private static int ticksRemaining = 0;

    public static void updateTarget(Vec3d direction, double speed) {
        targetWindDirection = direction;
        targetWindSpeed = speed;


        ticksRemaining = Math.max( SkydivingClientConfig.ticksPerWindChange / 3 , 1);




        // Initialize current if it's zero-length
        if (currentWindDirection.lengthSquared() < 0.0001) {
            currentWindDirection = direction.normalize().multiply(0.0001);
        }
    }

    public static void tick() {
        if (ticksRemaining <= 0) return;

        int transitionTicks = Math.max(1, SkydivingClientConfig.ticksPerWindChange / 3);
        double t = 1.0 - (ticksRemaining / (double) transitionTicks);

        Vec3d from = currentWindDirection.normalize();
        Vec3d to = targetWindDirection.normalize();

        Vec3d interpolatedDirection = from.multiply(1.0 - t).add(to.multiply(t)).normalize();
        double interpolatedSpeed = lerp(currentWindSpeed, targetWindSpeed, t);



        ChatUtils.write(String.format("ticks=%d/%d t=%.2f x=%.2f z=%.2f", ticksRemaining, transitionTicks, t, interpolatedDirection.x, interpolatedDirection.z));


        currentWindDirection = interpolatedDirection;
        currentWindSpeed = interpolatedSpeed;
        ticksRemaining--;
    }

    public static Vec3d getWindDirection() {
        return currentWindDirection;
    }

    public static double getWindSpeed() {
        return currentWindSpeed;
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
