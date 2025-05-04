package com.example.skydiving;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class WindChange {
    public final Vec3d direction;
    public final double speed;

    public WindChange(Vec3d direction, double speed) {
        this.direction = direction;
        this.speed = speed;
    }
}
