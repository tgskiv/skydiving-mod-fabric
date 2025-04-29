package com.example;

import com.example.skydiving.network.WindSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class SkydivingModClient implements ClientModInitializer {

	private static final MinecraftClient mc = MinecraftClient.getInstance();

	// Example wind vector: blowing NW at high speed
	private static Vec3d windDirection = Vec3d.ZERO;
	private static double windSpeed = 0.0;


	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (mc.player != null && mc.player.isFallFlying()) {
				applyWindToPlayer(mc.player);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(
				new Identifier("skydivingmod", "wind_sync"),
				(client, handler, buf, responseSender) -> {
					WindSyncPacket packet = WindSyncPacket.read(buf);
					client.execute(() -> {
						windDirection = packet.windDirection;
						windSpeed = packet.windSpeed;
					});
				}
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (mc.player != null && mc.player.isFallFlying() && !(mc.player.isTouchingWater() || mc.player.isSubmergedInWater())) {
				applyWindToPlayer(mc.player);
			}
		});

		System.out.println("Hello World from my first client Fabric mod!");
	}

	private void applyWindToPlayer(ClientPlayerEntity player) {
		if (windSpeed <= 0) return;

		Vec3d currentVelocity = player.getVelocity();
		Vec3d push = windDirection.multiply(windSpeed);

		Vec3d boosted = new Vec3d(
				currentVelocity.x + push.x,
				currentVelocity.y,
				currentVelocity.z + push.z
		);

		player.setVelocity(boosted);
	}
}