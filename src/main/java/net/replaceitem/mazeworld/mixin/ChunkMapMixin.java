package net.replaceitem.mazeworld.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.replaceitem.mazeworld.RecordRecoderRegistration;
import net.replaceitem.mazeworld.fakes.LevelStemAccess;
import net.replaceitem.mazeworld.fakes.ServerLevelAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @Inject(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/server/level/ThreadedLevelLightEngine;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/chunk/LevelChunk$UnsavedListener;)Lnet/minecraft/world/level/chunk/status/WorldGenContext;"))
    void beforeWorldGenContextInit(
           final ServerLevel level,
           final LevelStorageSource.LevelStorageAccess levelStorage,
           final DataFixer dataFixer,
           final StructureTemplateManager structureManager,
           final Executor executor,
           final BlockableEventLoop<Runnable> mainThreadExecutor,
           final LightChunkGetter chunkGetter,
           final ChunkGenerator generator,
           final ChunkStatusUpdateListener chunkStatusListener,
           final TicketStorage ticketStorage,
           final int serverViewDistance,
           final boolean syncWrites,
           CallbackInfo ci
    ) {
        var stem = ServerLevelAccess.LEVEL_STEM.get();
        var mazeGeneratorConfig = ((LevelStemAccess)(Object) stem).getMazeGenerator();
        if(mazeGeneratorConfig != null) {
            var mazeGenerator = mazeGeneratorConfig.createGenerator();
            RecordRecoderRegistration.WORLD_GEN_CONTEXT_MAZE_GENERATOR.queueNext(mazeGenerator);
        }
    }
}
