package com.example.skydiving.block;

import com.example.skydiving.blockentity.WindsockBlockEntity;
import com.example.skydiving.registry.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class WindsockBlock extends BlockWithEntity implements BlockEntityProvider {

    // Define the shape of the block (optional, but good for collision)
    // Adjust these values based on your model's dimensions
    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.3, 0.0, 0.3, 0.7, 1.5, 0.7); // Example shape

    public WindsockBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // Important: We use a BlockEntityRenderer, so the block itself renders as invisible.
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WindsockBlockEntity(pos, state);
    }

    // Optional: If your BlockEntity needs ticking (e.g., for animations independent of wind)
    // For simple rotation based on external wind data, ticking might not be necessary here.
    /*
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.WINDSOCK_BLOCK_ENTITY, WindsockBlockEntity::tick);
    }
    */
}