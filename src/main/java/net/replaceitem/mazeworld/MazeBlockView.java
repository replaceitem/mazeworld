package net.replaceitem.mazeworld;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class MazeBlockView<T extends BlockGetter> implements BlockGetter {
    
    protected final T delegate;
    protected final Block wallBlock;

    public MazeBlockView(T delegate, Block wallBlock) {
        this.delegate = delegate;
        this.wallBlock = wallBlock;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return delegate.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int y = pos.getY();
        if(y < this.getMinY()) {
            BlockState bottomBlock = delegate.getBlockState(pos.atY(this.getMinY()));
            if(bottomBlock.is(wallBlock)) return bottomBlock;
        } else if(y > this.getMaxY()) {
            BlockState topBlock = delegate.getBlockState(pos.atY(this.getMaxY()));
            if(topBlock.is(wallBlock)) return topBlock;
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
