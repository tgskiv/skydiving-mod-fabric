package com.example;


import com.example.skydiving.network.WindSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.math.Vec3d;

public class SkydivingModClient implements ClientModInitializer {

	private static final MinecraftClient mc = MinecraftClient.getInstance();

	// Example wind vector: blowing NW at high speed
	private static Vec3d windDirection = Vec3d.ZERO;
	private static double windSpeed = 0.0;


	public static Vec3d getWindDirection() {
		return windDirection;
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


//		// Register the Block Entity Renderer
//		BlockEntityRendererRegistry.register(ModBlockEntities.WINDSOCK_BLOCK_ENTITY, WindsockRenderer::new);
//
//		// Register the Model Layer Definition
//		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WINDSOCK_LAYER, WindsockModel::getTexturedModelData);

//		BlockEntityRendererFactories.register(DEMO_BLOCK_ENTITY);

		System.out.println("Hello World from my first client Fabric mod!");
	}

	private void applyWindToPlayer(ClientPlayerEntity player) {
		if (windSpeed <= 0) return;

		Vec3d push = windDirection.multiply(windSpeed);

		player.addVelocity(push);
	}
}