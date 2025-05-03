package com.example.skydiving.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import static com.example.SkydivingMod.MOD_ID;

public class WindSyncPacket {
    public static final Identifier WIND_PACKET_ID = new Identifier(MOD_ID, "wind_sync");

    public final Vec3d windDirection;
    public final double windSpeed;

    public WindSyncPacket(Vec3d windDirection, double windSpeed) {
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
    }

    public static WindSyncPacket read(PacketByteBuf buf) {
        Vec3d dir = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        double speed = buf.readDouble();
        return new WindSyncPacket(dir, speed);
    }

    public void write(PacketByteBuf buf) {
        buf.writeDouble(windDirection.x);
        buf.writeDouble(windDirection.y);
        buf.writeDouble(windDirection.z);
        buf.writeDouble(windSpeed);
    }
}