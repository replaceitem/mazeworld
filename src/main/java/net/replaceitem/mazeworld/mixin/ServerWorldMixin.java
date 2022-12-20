package net.replaceitem.mazeworld.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;
import net.replaceitem.mazeworld.MazeChunkGenerator;
import net.replaceitem.mazeworld.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldAccess {
    
    private boolean infiniteMazeWall = false;
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void storeInfiniteMaze(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime, CallbackInfo ci) {
        if(dimensionOptions.chunkGenerator() instanceof MazeChunkGenerator mazeChunkGenerator) {
            infiniteMazeWall = mazeChunkGenerator.getConfig().infiniteWall;
        }
    }

    @Override
    public boolean isInfiniteMaze() {
        return infiniteMazeWall;
    }
}
