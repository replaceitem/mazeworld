package net.replaceitem.mazeworld.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.replaceitem.mazeworld.LegacyMazeChunkGenerator;
import net.replaceitem.mazeworld.MazeCollisionView;
import net.replaceitem.mazeworld.fakes.LevelStemAccess;
import net.replaceitem.mazeworld.fakes.ServerLevelAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements ServerLevelAccess {
    protected ServerLevelMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Unique
    private boolean infiniteMazeWall = false;
    @Unique
    private Block mazeWallBlock = Blocks.BEDROCK;
    @Unique @Nullable
    private MazeCollisionView mazeCollisionView;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void storeInfiniteMaze(
            MinecraftServer server,
            Executor executor,
            LevelStorageSource.LevelStorageAccess levelStorage,
            ServerLevelData levelData,
            ResourceKey<Level> dimension,
            LevelStem levelStem,
            boolean isDebug,
            long biomeZoomSeed,
            List<CustomSpawner> customSpawners,
            boolean tickTime,
            CallbackInfo ci
    ) {
        var mazeGeneratorConfig = ((LevelStemAccess)(Object) levelStem).getMazeGenerator();
        if(mazeGeneratorConfig != null) {
            this.infiniteMazeWall = mazeGeneratorConfig.infiniteWall();
            this.mazeWallBlock = this.registryAccess().lookupOrThrow(Registries.BLOCK).getOptional(mazeGeneratorConfig.wallBlock()).orElse(Blocks.BEDROCK);
            this.mazeCollisionView = new MazeCollisionView(this, getMazeWallBlock());
        } else if(levelStem.generator() instanceof LegacyMazeChunkGenerator legacyMazeChunkGenerator) {
            var legacyConfig = legacyMazeChunkGenerator.getConfig();
            this.infiniteMazeWall = legacyConfig.infiniteWall;
            this.mazeWallBlock = this.registryAccess().lookupOrThrow(Registries.BLOCK).getOptional(legacyConfig.wallBlock).orElse(Blocks.BEDROCK);
            this.mazeCollisionView = new MazeCollisionView(this, getMazeWallBlock());
        }
    }

    @Unique
    @Override
    public boolean isInfiniteMaze() {
        return infiniteMazeWall;
    }

    @Unique
    @Override
    public Block getMazeWallBlock() {
        return mazeWallBlock;
    }

    @Override
    public Iterable<VoxelShape> getBlockCollisionsFromContext(CollisionContext shapeContext, AABB box) {
        if(isInfiniteMaze() && mazeCollisionView != null) return mazeCollisionView.getBlockCollisionsFromContext(shapeContext, box);
        return super.getBlockCollisionsFromContext(shapeContext, box);
    }


    @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/chunk/ChunkGenerator;IIZLnet/minecraft/world/level/entity/ChunkStatusUpdateListener;Ljava/util/function/Supplier;)Lnet/minecraft/server/level/ServerChunkCache;"))
    private ServerChunkCache beforeServerChunkCacheInit(
            ServerLevel level,
            LevelStorageSource.LevelStorageAccess levelStorage,
            DataFixer fixerUpper,
            StructureTemplateManager structureTemplateManager,
            Executor executor,
            ChunkGenerator generator,
            int viewDistance,
            int simulationDistance,
            boolean syncWrites,
            ChunkStatusUpdateListener chunkStatusListener,
            Supplier<SavedDataStorage> overworldDataStorage,
            Operation<ServerChunkCache> original,
            @Local(argsOnly = true, name = "levelStem") LevelStem levelStem
    ) {
        return ScopedValue.where(LEVEL_STEM, levelStem).call(() -> {
            // same args
            return original.call(level, levelStorage, fixerUpper, structureTemplateManager, executor, generator, viewDistance, simulationDistance, syncWrites, chunkStatusListener, overworldDataStorage);
        });
    }
}
