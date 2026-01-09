package net.replaceitem.mazeworld;

import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class MazeGenerator<T extends MazeGenerator2D.BlockChecker2D> {

    protected final MazeChunkGeneratorConfig config;

    protected MazeGenerator(MazeChunkGeneratorConfig config) {
        this.config = config;
    }
    
    public abstract void generateChunk(WorldGenLevel world, ChunkAccess chunk);
    
    public static void clearBlockEntities(ChunkAccess chunk, Block replacingBlock) {
        Set<BlockPos> blockEntityPositions = chunk.getBlockEntitiesPos();
        for (BlockPos pos : blockEntityPositions) {
            if(chunk.getBlockState(pos).is(replacingBlock)) {
                chunk.removeBlockEntity(pos);
            }
        }
    }

    public abstract T getBlockChecker(long seed);

    public static Random getMultiSeededRandom(long seed, int... ints) {
        for (int num : ints) {
            seed = new Random(seed+num).nextLong();
        }
        return new Random(seed);
    }

    public static int getRandomIntAt(int x, int y, long seed, int max) {
        return Math.abs(getMultiSeededRandom(seed, x, y).nextInt(max));
    }

    protected BlockState getWallBlockState(LevelAccessor world) {
        return world.registryAccess().lookupOrThrow(Registries.BLOCK).getOptional(this.config.wallBlock).orElse(Blocks.BEDROCK).defaultBlockState();
    }
}
