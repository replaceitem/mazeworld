package net.replaceitem.mazeworld;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class MazeBlockView<T extends BlockView> implements BlockView {
    
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
        if(y < this.getBottomY()) {
            BlockState bottomBlock = delegate.getBlockState(pos.withY(this.getBottomY()));
            if(bottomBlock.isOf(wallBlock)) return bottomBlock;
        } else if(y > this.getTopYInclusive()) {
            BlockState topBlock = delegate.getBlockState(pos.withY(this.getTopYInclusive()));
            if(topBlock.isOf(wallBlock)) return topBlock;
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
    public int getBottomY() {
        return delegate.getBottomY();
    }
}
