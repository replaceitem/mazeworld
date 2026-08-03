package net.replaceitem.mazeworld.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.replaceitem.mazeworld.config.MazeGeneratorConfig;
import net.replaceitem.mazeworld.worldgen.maze.MazeGenerator;

import java.util.Set;

public class MazeChunkGenerator {
    protected final MazeGeneratorConfig mazeGeneratorConfig;
    private final MazeGenerator mazeGenerator;

    public MazeChunkGenerator(MazeGeneratorConfig mazeGeneratorConfig, MazeGenerator mazeGenerator) {
        this.mazeGeneratorConfig = mazeGeneratorConfig;
        this.mazeGenerator = mazeGenerator;
    }

    public void generateChunk(WorldGenLevel world, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        long worldSeed = world.getSeed();

        int minX = chunkPos.getMinBlockX();
        int minY = Math.max(world.getMinY(), mazeGeneratorConfig.minY());
        int minZ = chunkPos.getMinBlockZ();

        int maxX = chunkPos.getMaxBlockX();
        int maxY = Math.min(world.getMaxY(), mazeGeneratorConfig.maxY() - 1);
        int maxZ = chunkPos.getMaxBlockZ();

        var state = this.getWallBlockState(world);
        var blockChecker = this.mazeGenerator.getBlockChecker(worldSeed);

        var mazePlacer = new MazePlacer(chunk, minX, minY, minZ, maxX, maxY, maxZ);

        if(mazeGeneratorConfig.replace() != BlockPredicate.alwaysTrue()) {
            var predicate = mazeGeneratorConfig.replace();
            mazePlacer.addCondition(pos -> predicate.test(world, pos));
        }

        var structureCondition = new StructureCondition(mazeGeneratorConfig.replaceStructures(), world, chunkPos);
        structureCondition.createPredicate().ifPresent(mazePlacer::addCondition);

        if(mazeGenerator.is3d()) {
            mazePlacer.generate3d(blockChecker, state);
        } else {
            mazePlacer.generate2d(blockChecker, state);
        }

        clearBlockEntities(chunk, state.getBlock());
    }
    
    public static void clearBlockEntities(ChunkAccess chunk, Block replacingBlock) {
        Set<BlockPos> blockEntityPositions = chunk.getBlockEntitiesPos();
        for (BlockPos pos : blockEntityPositions) {
            if(chunk.getBlockState(pos).is(replacingBlock)) {
                chunk.removeBlockEntity(pos);
            }
        }
    }

    protected BlockState getWallBlockState(LevelAccessor world) {
        return world.registryAccess().lookupOrThrow(Registries.BLOCK).getOptional(this.mazeGeneratorConfig.wallBlock()).orElse(Blocks.BEDROCK).defaultBlockState();
    }
}
