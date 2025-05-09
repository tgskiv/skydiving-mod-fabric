package com.tgskiv;


import com.tgskiv.skydiving.blocks.ModModelLayers;
import com.tgskiv.skydiving.blocks.WindsockModel;
import com.tgskiv.skydiving.blockentity.WindsockBlockEntity;
import com.tgskiv.skydiving.flight.FlightUtils;
import com.tgskiv.skydiving.network.WindSyncPayload;
import com.tgskiv.skydiving.registry.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.math.Vec3d;
import com.tgskiv.skydiving.blocks.WindsockBlockEntityRenderer;


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
//		ClientTickEvents.END_CLIENT_TICK.register(client -> {
//			if (mc.player != null && mc.player.isFallFlying()) {
//				FlightUtils.applyWindToPlayer(mc.player, windDirection, windSpeed);
//			}
//		});

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
				FlightUtils.applyWindToPlayer(mc.player, windDirection, windSpeed);
			}
		});


		BlockEntityRendererFactories.register(
				ModBlockEntities.WINDSOCK_BLOCK_ENTITY,
//				WindsockBlockEntityRenderer::new

				// This all needed to render cone from a far, as by default
				// it renders 64 blocks far max
				ctx -> new WindsockBlockEntityRenderer(ctx) {
					@Override
					public boolean rendersOutsideBoundingBox(WindsockBlockEntity blockEntity) {
						return true;
					}

					@Override
					public int getRenderDistance() {
						return 256;
					}
				}
		);
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WINDSOCK_LAYER, WindsockModel::getTexturedModelData);

		System.out.println("Hello World from my first client Fabric mod!");
	}

}