package com.example.skydiving.model;

import net.minecraft.client.model.*;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static com.example.SkydivingMod.MOD_ID;

// Represents the visual model of the windsock
public class WindsockModel extends Model {

    private final ModelPart stick; // The static part (stick)
    private final ModelPart cone1;
    private final ModelPart cone2;
    private final ModelPart cone3;
    private final ModelPart cone4;

    // Texture Identifiers (adjust paths as needed)
    public static final Identifier STICK_TEXTURE = new Identifier(MOD_ID, "textures/entity/brown_stick_texture.png");
    public static final Identifier RED_CONE_TEXTURE = new Identifier(MOD_ID, "textures/entity/red_cone_texture.png");
    public static final Identifier WHITE_CONE_TEXTURE = new Identifier(MOD_ID, "textures/entity/white_cone_texture.png");


    public WindsockModel(ModelPart root) {
        // Use RenderLayer.getEntitySolid for opaque parts like the stick
        // Use RenderLayer.getEntityCutout or Translucent for parts with transparency if needed

        // Get the parts from the root ModelPart provided by the layer definition
        // Ensure these names ("base", "cone") match what you use in getTexturedModelData()
        this.stick = root.getChild("stick");
        this.cone1 = root.getChild("cone1");
        this.cone2 = root.getChild("cone2");
        this.cone3 = root.getChild("cone3");
        this.cone4 = root.getChild("cone4");
    }

    // This method defines the model structure and texture mapping.
    // You'll need to adapt this based on the exact structure Blockbench gave you.
    // The key is to separate the 'base' (stick) and 'cone' parts.
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // --- Base (Stick) ---
        // Example: A simple vertical stick
        // Adjust dimensions, position, and UV mapping based on your Blockbench model and texture
        ModelPartData stickPart = root.addChild("stick", ModelPartBuilder.create()
                        .uv(0, 0) // UV coordinates on brown_stick_texture.png
                        .cuboid(-1.0F, -24.0F, -1.0F, 2.0F, 24.0F, 2.0F), // x, y, z, dx, dy, dz
                ModelTransform.pivot(0.0F, 24.0F, 0.0F)); // Pivot point at the bottom center

        // --- Cone ---
        // This part will rotate. Define its geometry relative to its own pivot.
        // The pivot point for the cone should be where it attaches to the stick.
        // Example: A simple cone made of multiple cuboids (adjust!)
        // You will likely have multiple cuboid calls here based on your Blockbench export.
        // Make sure the UVs map correctly to your red and white textures.
        ModelPartData cone1Part = root.addChild("cone1", ModelPartBuilder.create()
                        // Example element 1 (Red part) - Adjust UVs for red_cone_texture.png
                        .uv(0, 0).cuboid(-4.0F, -23.0F, -4.0F, 8.0F, 4.0F, 8.0F)
                        // Example element 2 (White part) - Adjust UVs for white_cone_texture.png
                        .uv(0, 12).cuboid(-3.0F, -19.0F, -3.0F, 6.0F, 4.0F, 6.0F)
                        // Add more cuboids as needed...
                        // Example element 3 (Red part)
                        .uv(0, 22).cuboid(-2.0F, -15.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                // Pivot should be at the top-center of the stick model part
                // If stick top is at Y=0 (relative to base pivot), this pivot is correct.
                // Adjust Y if needed (-24.0F matches the stick's top in the example)
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        // Example: A simple cone made of multiple cuboids (adjust!)
        // You will likely have multiple cuboid calls here based on your Blockbench export.
        // Make sure the UVs map correctly to your red and white textures.
        ModelPartData cone2Part = root.addChild("cone2", ModelPartBuilder.create()
                        // Example element 1 (Red part) - Adjust UVs for red_cone_texture.png
                        .uv(0, 0).cuboid(-4.0F, -23.0F, -4.0F, 8.0F, 4.0F, 8.0F)
                        // Example element 2 (White part) - Adjust UVs for white_cone_texture.png
                        .uv(0, 12).cuboid(-3.0F, -19.0F, -3.0F, 6.0F, 4.0F, 6.0F)
                        // Add more cuboids as needed...
                        // Example element 3 (Red part)
                        .uv(0, 22).cuboid(-2.0F, -15.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                // Pivot should be at the top-center of the stick model part
                // If stick top is at Y=0 (relative to base pivot), this pivot is correct.
                // Adjust Y if needed (-24.0F matches the stick's top in the example)
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        // Example: A simple cone made of multiple cuboids (adjust!)
        // You will likely have multiple cuboid calls here based on your Blockbench export.
        // Make sure the UVs map correctly to your red and white textures.
        ModelPartData cone3Part = root.addChild("cone3", ModelPartBuilder.create()
                        // Example element 1 (Red part) - Adjust UVs for red_cone_texture.png
                        .uv(0, 0).cuboid(-4.0F, -23.0F, -4.0F, 8.0F, 4.0F, 8.0F)
                        // Example element 2 (White part) - Adjust UVs for white_cone_texture.png
                        .uv(0, 12).cuboid(-3.0F, -19.0F, -3.0F, 6.0F, 4.0F, 6.0F)
                        // Add more cuboids as needed...
                        // Example element 3 (Red part)
                        .uv(0, 22).cuboid(-2.0F, -15.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                // Pivot should be at the top-center of the stick model part
                // If stick top is at Y=0 (relative to base pivot), this pivot is correct.
                // Adjust Y if needed (-24.0F matches the stick's top in the example)
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        // Example: A simple cone made of multiple cuboids (adjust!)
        // You will likely have multiple cuboid calls here based on your Blockbench export.
        // Make sure the UVs map correctly to your red and white textures.
        ModelPartData cone4Part = root.addChild("cone4", ModelPartBuilder.create()
                        // Example element 1 (Red part) - Adjust UVs for red_cone_texture.png
                        .uv(0, 0).cuboid(-4.0F, -23.0F, -4.0F, 8.0F, 4.0F, 8.0F)
                        // Example element 2 (White part) - Adjust UVs for white_cone_texture.png
                        .uv(0, 12).cuboid(-3.0F, -19.0F, -3.0F, 6.0F, 4.0F, 6.0F)
                        // Add more cuboids as needed...
                        // Example element 3 (Red part)
                        .uv(0, 22).cuboid(-2.0F, -15.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                // Pivot should be at the top-center of the stick model part
                // If stick top is at Y=0 (relative to base pivot), this pivot is correct.
                // Adjust Y if needed (-24.0F matches the stick's top in the example)
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));


        return TexturedModelData.of(modelData, 64, 64); // Adjust texture width/height if needed
    }

    // Render the static base (stick)
    public void renderBase(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.stick.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    // Render the rotating cone
    public void renderCone(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.cone1.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.cone2.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.cone3.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.cone4.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }


    // Deprecated render method - we'll use custom methods in the BER
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        // We won't use this directly, the BlockEntityRenderer will call renderBase/renderCone
        // If needed for item rendering, you might implement this.
        // this.base.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        // this.cone.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    // Optional: If you need to animate parts *within* the model (e.g., flapping)
    // public void setAngles(WindsockBlockEntity entity, float tickDelta) {
    //    // Set rotation/translation of individual ModelParts based on entity state or time
    // }
}