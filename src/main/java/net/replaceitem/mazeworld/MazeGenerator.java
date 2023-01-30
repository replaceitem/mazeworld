package net.replaceitem.mazeworld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public abstract class MazeGenerator {

    protected final MazeChunkGeneratorConfig config;

    protected MazeGenerator(MazeChunkGeneratorConfig config) {
        this.config = config;
    }

    public void generateChunk(StructureWorldAccess world, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        long worldSeed = world.getSeed();
        int xs = chunkPos.getStartX();
        int zs = chunkPos.getStartZ();
        int xe = chunkPos.getEndX();
        int ze = chunkPos.getEndZ();

        BlockChecker blockChecker = getBlockChecker(worldSeed);

        int wallTopY = world.getBottomY() + world.getDimension().logicalHeight();

        for(int i = xs; i <= xe; i++) {
            for(int j = zs; j <= ze; j++) {
                if(blockChecker.isBlockAt(i, j))
                    placeColumn(world, chunk, i, j, wallTopY, Blocks.BEDROCK.getDefaultState());
            }
        }

    }

    protected static void placeColumn(StructureWorldAccess world, Chunk chunk, int cx, int cz, int top, BlockState blockState) {
        BlockPos.Mutable pos = new BlockPos.Mutable(cx, world.getBottomY(), cz);
        while(pos.getY() < top) {
            setBlock(chunk, pos, blockState);
            pos.setY(pos.getY()+1);
        }
    }

    protected static void setBlock(Chunk chunk, BlockPos pos, BlockState blockState) {
        chunk.setBlockState(pos, blockState, false);
        chunk.removeBlockEntity(pos);
    }

    public abstract BlockChecker getBlockChecker(long seed);

    @FunctionalInterface
    public interface BlockChecker {
        boolean isBlockAt(int x, int y);
    }

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
