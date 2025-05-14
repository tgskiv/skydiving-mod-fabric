package com.tgskiv.skydiving.flight;

import com.tgskiv.skydiving.menu.SkydivingClientConfig;
import net.minecraft.util.math.Vec3d;

public class WindInterpolator {



    private static Vec3d fromWindDirection = Vec3d.ZERO;
    private static Vec3d toWindDirection = Vec3d.ZERO;
    private static Vec3d currentWindDirection = Vec3d.ZERO;


    private static double pastWindSpeed = 0.0;
    private static double currentWindSpeed = 0.0;
    private static double targetWindSpeed = 0.0;


    private static int ticksRemaining = 0;
    private static int ticksToTransition = 400;


    public static Vec3d getWindDirection() {
        return currentWindDirection;
    }

    public static double getWindSpeed() {
        return currentWindSpeed;
    }


    public static void updateTarget(Vec3d newDirection, double newSpeed) {

        fromWindDirection = new Vec3d(toWindDirection.x, toWindDirection.y, toWindDirection.z).normalize();
        currentWindDirection = new Vec3d(toWindDirection.x, toWindDirection.y, toWindDirection.z).normalize();
        toWindDirection = new Vec3d(newDirection.x, newDirection.y, newDirection.z).normalize();

        pastWindSpeed = targetWindSpeed;
        currentWindSpeed = targetWindSpeed;
        targetWindSpeed = newSpeed;


        ticksToTransition = Math.max( SkydivingClientConfig.ticksPerWindChange / 3 , 1);
        ticksRemaining = ticksToTransition;


        // Initialize current if it's zero-length
        if (newDirection.lengthSquared() < 0.0001) {
            currentWindDirection = newDirection.normalize().multiply(0.0001);
        }
    }

    public static void tick() {
        if (ticksRemaining <= 0) return;

        double t = 1.0 - (ticksRemaining / (double) ticksToTransition); // Start with 0, then 0.5, ends with 1

        currentWindDirection = slerp(fromWindDirection, toWindDirection, t);
        currentWindSpeed = lerp(pastWindSpeed, targetWindSpeed, t);

//        ChatUtils.write(String.format("ticks=%d/%d t=%.2f x=%.2f z=%.2f", ticksRemaining, ticksToTransition, t, currentWindDirection.x, currentWindDirection.z));

        ticksRemaining--;
    }


    public static Vec3d slerp(Vec3d from, Vec3d to, double fraction) {
        double dot = from.dotProduct(to);

        // Clamp dot product toWindSpeed avoid numerical errors
        dot = Math.min(Math.max(dot, -1.0), 1.0);

        double theta = Math.acos(dot) * fraction;

        Vec3d relativeVec = to.subtract(from.multiply(dot)).normalize();
        return from.multiply(Math.cos(theta)).add(relativeVec.multiply(Math.sin(theta)));
    }

    private static Vec3d rotateAroundAxis(Vec3d vec, Vec3d axis, double angle) {
        return vec.multiply(Math.cos(angle))
                .add(axis.crossProduct(vec).multiply(Math.sin(angle)))
                .add(axis.multiply(axis.dotProduct(vec) * (1 - Math.cos(angle))));
    }


    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
