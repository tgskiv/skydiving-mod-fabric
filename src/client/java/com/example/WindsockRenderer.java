package com.example; // Adjust package to match yours

import com.example.skydiving.blockentity.WindsockBlockEntity;
import com.example.skydiving.model.WindsockModel;
import com.example.skydiving.registry.ModModelLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;


public class WindsockRenderer implements BlockEntityRenderer<WindsockBlockEntity> {

    private final WindsockModel model;

    public WindsockRenderer(BlockEntityRendererFactory.Context ctx) {
        // Get the model instance from the registered layer
        this.model = new WindsockModel(ctx.getLayerModelPart(ModModelLayers.WINDSOCK_LAYER));
    }

    @Override
    public void render(WindsockBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        // --- Get Wind Data ---
        Vec3d windDir = SkydivingModClient.getWindDirection();
        // double windSpeed = SkydivingModClient.getWindSpeed(); // Use if needed for animation/texture

        // --- Calculate Rotation ---
        float windYaw = 0.0f;
        if (windDir.lengthSquared() > 1e-6) { // Avoid calculating angle for zero vector
            // Calculate the angle in the horizontal plane (X-Z)
            // atan2 gives the angle relative to the positive Z axis (south)
            // We add PI (180 degrees) because Minecraft's default rotation (0) faces South.
            // The result is negated because RotationAxis.POSITIVE_Y rotates counter-clockwise.
            windYaw = (float) (-MathHelper.atan2(windDir.x, windDir.z));
        }

        // --- Rendering Setup ---
        matrices.push();

        // Center the model in the block space and adjust position if needed
        // The model's pivot points handle relative positioning, but this centers the whole thing.
        matrices.translate(0.5, 1.5, 0.5); // Translate to center top of block
        // Rotate 180 degrees on Y axis because default model orientation faces South
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
        // Scale if necessary (e.g., if the model is too big/small)
        // matrices.scale(1.0f, 1.0f, 1.0f);
        // Flip vertically because block models are often inverted compared to entity models
        matrices.scale(1.0f, -1.0f, -1.0f);


        // --- Render Static Base (Stick) ---
        // Get the vertex consumer for the stick texture
        VertexConsumer stickConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(WindsockModel.STICK_TEXTURE)); // Use cutout if texture has transparency
        model.renderBase(matrices, stickConsumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);


        // --- Render Rotating Cone ---
        matrices.push(); // Save matrix state before applying rotation for the cone

        // Apply wind rotation around the Y axis
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(windYaw));

        // Get vertex consumers for the cone textures
        // You might need separate consumers if red/white parts use different textures
        // Or, if your model uses one texture sheet, adjust UVs in the model definition.
        VertexConsumer redConeConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(WindsockModel.RED_CONE_TEXTURE));
        VertexConsumer whiteConeConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(WindsockModel.WHITE_CONE_TEXTURE));

        // Render the cone - THIS ASSUMES YOUR MODEL RENDERS CORRECTLY WITH MULTIPLE TEXTURES
        // If your WindsockModel's `renderCone` method handles switching textures internally,
        // you might only need one consumer type here (e.g., getEntityCutout).
        // If not, you might need to modify WindsockModel to accept multiple consumers or
        // render specific parts (e.g., model.renderRedPart(...), model.renderWhitePart(...))
        // For simplicity now, we pass one consumer. Adapt if needed.
        // We'll use the red texture consumer here, assuming the model handles UVs for both.
        model.renderCone(matrices, redConeConsumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
        // If you need separate consumers:
        // model.renderRedParts(matrices, redConeConsumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
        // model.renderWhiteParts(matrices, whiteConeConsumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);


        matrices.pop(); // Restore matrix state after cone rendering


        // --- Final Cleanup ---
        matrices.pop(); // Restore matrix state after all rendering
    }

    // Optional: Determines if the renderer should render from a distance
    @Override
    public boolean rendersOutsideBoundingBox(WindsockBlockEntity blockEntity) {
        return true; // Render even if the block's bounding box isn't visible
    }

    // Optional: Get render distance
    @Override
    public int getRenderDistance() {
        return 128; // Default is usually 64, increase if needed
    }
}