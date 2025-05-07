package com.example.skydiving;

import com.example.SkydivingModClient;
import com.example.skydiving.blockentity.WindsockBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class WindsockBlockEntityRenderer implements BlockEntityRenderer<WindsockBlockEntity> {

    private final WindsockModel model;

    public WindsockBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new WindsockModel(ctx.getLayerModelPart(ModModelLayers.WINDSOCK_LAYER));
    }

    @Override
    public void render(WindsockBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Vec3d windDir = SkydivingModClient.getWindDirection();

        if (!windDir.equals(Vec3d.ZERO)) {
            // Calculate the angle of rotation based on the wind direction.
            // We want the windsock to point in the direction the wind is coming *from*.
            // So, we'll use the negative of the wind direction.
            double angleRadians = Math.atan2(-windDir.x, -windDir.z);
            float angleDegrees = (float) Math.toDegrees(angleRadians);

            matrices.push();
            // Translate to the center of the block (adjust if your model's pivot is different)
            matrices.translate(0.5, 0.5, 0.5);
            // Rotate around the Y-axis
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-angleDegrees));
            // Translate back so the rotation is around the block's center
            matrices.translate(-0.5, -0.5, -0.5);


            model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(WindsockModel.STICK_TEXTURE)), light, overlay, Colors.RED);

            matrices.pop();
        } else {
            // Render the windsock in its default orientation if there's no wind
            matrices.push();
            model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(WindsockModel.STICK_TEXTURE)), light, overlay, Colors.RED);
            matrices.pop();
        }
    }


    @Override
    public boolean rendersOutsideBoundingBox(WindsockBlockEntity blockEntity) {
        return true; // Important for models that might extend beyond the block bounds
    }
}