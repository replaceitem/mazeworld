package net.replaceitem.mazeworld;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;
import java.util.Set;

public abstract class MazeGenerator<T extends MazeGenerator2D.BlockChecker2D> {

    protected final MazeChunkGeneratorConfig config;

    protected MazeGenerator(MazeChunkGeneratorConfig config) {
        this.config = config;
    }
    
    public abstract void generateChunk(StructureWorldAccess world, Chunk chunk);
    
    public static void clearBlockEntities(Chunk chunk) {
        Set<BlockPos> blockEntityPositions = chunk.getBlockEntityPositions();
        for (BlockPos pos : blockEntityPositions) {
            if(chunk.getBlockState(pos).isOf(Blocks.BEDROCK)) {
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
}
