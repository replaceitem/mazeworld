package net.replaceitem.mazeworld;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MazeCollisionView extends MazeBlockView<CollisionView> implements CollisionView {
    public MazeCollisionView(CollisionView delegate, Block wallBlock) {
        super(delegate, wallBlock);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return delegate.getWorldBorder();
    }

    @Override
    public @Nullable BlockView getChunkAsView(int chunkX, int chunkZ) {
        return new MazeBlockView<>(delegate.getChunkAsView(chunkX, chunkZ), wallBlock);
    }

    @Override
    public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box) {
        return delegate.getEntityCollisions(entity, box);
    }
}
