package net.replaceitem.mazeworld;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class InfiniteWallBlockView<T extends BlockGetter> implements BlockGetter {
    
    protected final T delegate;
    protected final InfiniteWallConfig infiniteWallConfig;

    public InfiniteWallBlockView(T delegate, InfiniteWallConfig infiniteWallConfig) {
        this.delegate = delegate;
        this.infiniteWallConfig = infiniteWallConfig;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return delegate.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int y = pos.getY();
        var minY = this.infiniteWallConfig.minY();
        var maxY = this.infiniteWallConfig.maxY();
        if(y < minY) {
            BlockState bottomBlock = delegate.getBlockState(pos.atY(minY));
            if(bottomBlock.is(infiniteWallConfig.mazeWallBlock())) return Blocks.BEDROCK.defaultBlockState();
        } else if(y >= maxY) {
            BlockState topBlock = delegate.getBlockState(pos.atY(maxY - 1));
            if(topBlock.is(infiniteWallConfig.mazeWallBlock())) return Blocks.BEDROCK.defaultBlockState();
        }
        return delegate.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return delegate.getFluidState(pos);
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public int getMinY() {
        return delegate.getMinY();
    }
}
