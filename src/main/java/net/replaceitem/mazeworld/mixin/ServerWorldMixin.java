package net.replaceitem.mazeworld.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.replaceitem.mazeworld.MazeChunkGenerator;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeCollisionView;
import net.replaceitem.mazeworld.fakes.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin extends Level implements ServerWorldAccess {

    protected ServerWorldMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Unique
    private boolean infiniteMazeWall = false;
    @Unique
    private Block mazeWallBlock = null;
    @Unique @Nullable
    private MazeCollisionView mazeCollisionView;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void storeInfiniteMaze(MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess session, ServerLevelData properties, ResourceKey<Level> worldKey, LevelStem dimensionOptions, boolean debugWorld, long seed, List<CustomSpawner> spawners, boolean shouldTickTime, RandomSequences randomSequenceState, CallbackInfo ci) {
        if(dimensionOptions.generator() instanceof MazeChunkGenerator mazeChunkGenerator) {
            MazeChunkGeneratorConfig config = mazeChunkGenerator.getConfig();
            infiniteMazeWall = config.infiniteWall;
            mazeWallBlock = this.registryAccess().lookupOrThrow(Registries.BLOCK).getOptional(config.wallBlock).orElse(Blocks.BEDROCK);
            this.mazeCollisionView = new MazeCollisionView(this, getMazeWallBlock());
        }
    }

    @Override
    public boolean isInfiniteMaze() {
        return infiniteMazeWall;
    }

    @Override
    public Block getMazeWallBlock() {
        return mazeWallBlock;
    }

    @Override
    public Iterable<VoxelShape> getBlockCollisionsFromContext(CollisionContext shapeContext, AABB box) {
        if(isInfiniteMaze() && mazeCollisionView != null) return mazeCollisionView.getBlockCollisionsFromContext(shapeContext, box);
        return super.getBlockCollisionsFromContext(shapeContext, box);
    }
}
