package net.replaceitem.mazeworld.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfiniteWallCollisionView extends InfiniteWallBlockView<CollisionGetter> implements CollisionGetter {
    public InfiniteWallCollisionView(CollisionGetter delegate, InfiniteWallConfig infiniteWallConfig) {
        super(delegate, infiniteWallConfig);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return delegate.getWorldBorder();
    }

    @Override
    public @Nullable BlockGetter getChunkForCollisions(int chunkX, int chunkZ) {
        @Nullable BlockGetter delegateChunkView = delegate.getChunkForCollisions(chunkX, chunkZ);
        if(delegateChunkView == null) return null;
        return new InfiniteWallBlockView<>(delegateChunkView, infiniteWallConfig);
    }

    @Override
    public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB box) {
        return delegate.getEntityCollisions(entity, box);
    }
}
