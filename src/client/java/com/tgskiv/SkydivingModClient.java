package com.tgskiv;


import com.tgskiv.skydiving.blocks.ModModelLayers;
import com.tgskiv.skydiving.blocks.WindsockModel;
import com.tgskiv.skydiving.flight.FlightUtils;
import com.tgskiv.skydiving.flight.WindInterpolator;
import com.tgskiv.skydiving.menu.SkydivingClientConfig;
import com.tgskiv.skydiving.network.ToggleAirflowDebugPayload;
import com.tgskiv.skydiving.network.WindConfigSyncPayload;
import com.tgskiv.skydiving.network.WindSyncPayload;
import com.tgskiv.skydiving.registry.ModBlockEntities;
import com.tgskiv.skydiving.ui.AirflowDebugOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.math.Vec3d;
import com.tgskiv.skydiving.blocks.WindsockBlockEntityRenderer;


public class SkydivingModClient implements ClientModInitializer {

	private static final MinecraftClient mc = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient() {

		// Subscribe to receiving the wind updates from the server
		ClientPlayNetworking.registerGlobalReceiver(
				WindSyncPayload.PAYLOAD_ID,
				(payload, context) ->
					context.client().execute(() -> {
						WindInterpolator.updateTarget(payload.direction(), payload.speed());
					})

		);

		// Subscribe to receiving the wind configuration on joining the world
		ClientPlayNetworking.registerGlobalReceiver(WindConfigSyncPayload.PAYLOAD_ID, (payload, context) ->
				context.client().execute(() -> {
					SkydivingClientConfig.ticksPerWindChange = payload.ticksPerWindChange();
					SkydivingClientConfig.windRotationDegrees = payload.windRotationDegrees();
					SkydivingClientConfig.maxSpeedDelta = payload.maxSpeedDelta();
					SkydivingClientConfig.maxWindSpeed = payload.maxWindSpeed();
					SkydivingClientConfig.minWindSpeed = payload.minWindSpeed();
				})
		);

		// Subscribe to Hud display command
		ClientPlayNetworking.registerGlobalReceiver(ToggleAirflowDebugPayload.PAYLOAD_ID, (payload, context) ->
				context.client().execute(() ->
					AirflowDebugOverlay.visible = payload.visible()
				)
		);


		// Apply wind on tick
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (mc.player == null) { return; }

			WindInterpolator.tick();

			boolean inFlight = (mc.player.isGliding() && !(mc.player.isTouchingWater() || mc.player.isSubmergedInWater()));

			if (inFlight) FlightUtils.applyWindToPlayer(mc.player, WindInterpolator.getWindDirection(), WindInterpolator.getWindSpeed());

			FlightUtils.getSpinFallEffect(mc.player);
			if (inFlight) FlightUtils.applySpinFallEffect(mc.player);

			FlightUtils.getUpdraftEffect(mc.player, WindInterpolator.getWindDirection());
			if (inFlight) FlightUtils.applyUpdraftEffect(mc.player);
		});

		// Register the Windsock Block Entity renderer
		BlockEntityRendererFactories.register(
				ModBlockEntities.WINDSOCK_BLOCK_ENTITY,
//				WindsockBlockEntityRenderer::new

				// This all needed to render cone from a far, as by default
				// it renders 64 blocks far max
				ctx -> new WindsockBlockEntityRenderer(ctx) {
					@Override
					public int getRenderDistance() {
						return 256;
					}
				}
		);
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WINDSOCK_LAYER, WindsockModel::getTexturedModelData);
		HudRenderCallback.EVENT.register(new AirflowDebugOverlay());

		System.out.println("Hello World from my first client Fabric mod!");
	}

}