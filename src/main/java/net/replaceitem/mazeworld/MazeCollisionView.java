package net.replaceitem.mazeworld;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MazeCollisionView extends MazeBlockView<CollisionGetter> implements CollisionGetter {
    public MazeCollisionView(CollisionGetter delegate, Block wallBlock) {
        super(delegate, wallBlock);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return delegate.getWorldBorder();
    }

    @Override
    public @Nullable BlockGetter getChunkForCollisions(int chunkX, int chunkZ) {
        @Nullable BlockGetter delegateChunkView = delegate.getChunkForCollisions(chunkX, chunkZ);
        if(delegateChunkView == null) return null;
        return new MazeBlockView<>(delegateChunkView, wallBlock);
    }

    @Override
    public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB box) {
        return delegate.getEntityCollisions(entity, box);
    }
}
