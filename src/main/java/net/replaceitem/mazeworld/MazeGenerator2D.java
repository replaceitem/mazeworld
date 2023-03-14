package net.replaceitem.mazeworld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

public abstract class MazeGenerator2D extends MazeGenerator<MazeGenerator2D.BlockChecker2D> {

    protected MazeGenerator2D(MazeChunkGeneratorConfig config) {
        super(config);
    }

    public void generateChunk(StructureWorldAccess world, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        long worldSeed = world.getSeed();
        int xs = chunkPos.getStartX();
        int zs = chunkPos.getStartZ();
        int xe = chunkPos.getEndX();
        int ze = chunkPos.getEndZ();

        BlockChecker2D blockChecker = getBlockChecker(worldSeed);

        int wallTopY = world.getTopY();

        for(int i = xs; i <= xe; i++) {
            for(int j = zs; j <= ze; j++) {
                if(blockChecker.isBlockAt(i, j))
                    placeColumn(world, chunk, i, j, wallTopY, Blocks.BEDROCK.getDefaultState());
            }
        }
        clearBlockEntities(chunk);
    }

    protected static void placeColumn(StructureWorldAccess world, Chunk chunk, int cx, int cz, int top, BlockState blockState) {
        BlockPos.Mutable pos = new BlockPos.Mutable(cx, world.getBottomY(), cz);
        while(pos.getY() < top) {
            chunk.setBlockState(pos, blockState, false);
            pos.setY(pos.getY()+1);
        }
    }

    @FunctionalInterface
    public interface BlockChecker2D {
        boolean isBlockAt(int x, int z);
    }
}