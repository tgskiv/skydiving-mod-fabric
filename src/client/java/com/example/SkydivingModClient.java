package com.example;


import com.example.skydiving.ModModelLayers;
import com.example.skydiving.WindsockModel;
import com.example.skydiving.network.WindSyncPayload;
import com.example.skydiving.registry.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.math.Vec3d;
import com.example.skydiving.WindsockBlockEntityRenderer;

public class SkydivingModClient implements ClientModInitializer {

	private static final MinecraftClient mc = MinecraftClient.getInstance();

	// Example wind vector: blowing NW at high speed
	private static Vec3d windDirection = Vec3d.ZERO;
	private static double windSpeed = 0.0;


	public static Vec3d getWindDirection() {
		return windDirection;
	}
	public static double getWindSpeed() {
		return windSpeed;
	}

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (mc.player != null && mc.player.isFallFlying()) {
				applyWindToPlayer(mc.player);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(
				WindSyncPayload.PACKET_ID,
				(payload, context) ->
					context.client().execute(() -> {
						windDirection = payload.direction();
						windSpeed = payload.speed();
					})

		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (mc.player != null && mc.player.isFallFlying() && !(mc.player.isTouchingWater() || mc.player.isSubmergedInWater())) {
				applyWindToPlayer(mc.player);
			}
		});


		BlockEntityRendererFactories.register(ModBlockEntities.WINDSOCK_BLOCK_ENTITY, WindsockBlockEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WINDSOCK_LAYER, WindsockModel::getTexturedModelData);

		System.out.println("Hello World from my first client Fabric mod!");
	}

	private void applyWindToPlayer(ClientPlayerEntity player) {
		if (windSpeed <= 0) return;

		Vec3d push = windDirection.multiply(windSpeed);

		player.addVelocity(push);
	}
}