package com.example.skydiving.blockentity;

import com.example.skydiving.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WindsockBlockEntity extends BlockEntity {

    public WindsockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WINDSOCK_BLOCK_ENTITY, pos, state);
    }

    // This BlockEntity doesn't need to store wind data itself,
    // as the renderer will fetch it directly from SkydivingModClient.
    // Add NBT read/write methods if you add stored properties later.

    // Optional tick method if needed
    /*
    public static void tick(World world, BlockPos pos, BlockState state, WindsockBlockEntity be) {
        if (!world.isClient()) {
           // Server-side logic if needed
        } else {
           // Client-side logic if needed (less common here, renderer handles visuals)
        }
    }
    */

}
