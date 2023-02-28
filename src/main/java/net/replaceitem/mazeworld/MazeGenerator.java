package net.replaceitem.mazeworld;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public abstract class MazeGenerator<T extends MazeGenerator2D.BlockChecker2D> {

    protected final MazeChunkGeneratorConfig config;

    protected MazeGenerator(MazeChunkGeneratorConfig config) {
        this.config = config;
    }
    
    public abstract void generateChunk(StructureWorldAccess world, Chunk chunk);

    protected static void setBlock(Chunk chunk, BlockPos pos, BlockState blockState) {
        chunk.setBlockState(pos, blockState, false);
        chunk.removeBlockEntity(pos);
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
