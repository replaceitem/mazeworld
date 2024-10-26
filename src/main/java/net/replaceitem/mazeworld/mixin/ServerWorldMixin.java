package net.replaceitem.mazeworld.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.SpecialSpawner;
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

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ServerWorldAccess {

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Unique
    private boolean infiniteMazeWall = false;
    @Unique
    private Block mazeWallBlock = null;
    @Unique @Nullable
    private MazeCollisionView mazeCollisionView;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void storeInfiniteMaze(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<SpecialSpawner> spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, CallbackInfo ci) {
        if(dimensionOptions.chunkGenerator() instanceof MazeChunkGenerator mazeChunkGenerator) {
            MazeChunkGeneratorConfig config = mazeChunkGenerator.getConfig();
            infiniteMazeWall = config.infiniteWall;
            mazeWallBlock = this.getRegistryManager().getOrThrow(RegistryKeys.BLOCK).getOptionalValue(config.wallBlock).orElse(Blocks.BEDROCK);
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
    public Iterable<VoxelShape> getBlockCollisions(@Nullable Entity entity, Box box) {
        if(isInfiniteMaze() && mazeCollisionView != null) return mazeCollisionView.getBlockCollisions(entity, box);
        return super.getBlockCollisions(entity, box);
    }
}
