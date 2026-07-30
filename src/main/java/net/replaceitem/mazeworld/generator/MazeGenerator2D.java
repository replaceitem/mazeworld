package net.replaceitem.mazeworld.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.replaceitem.mazeworld.MazeGeneratorConfig;

public abstract class MazeGenerator2D extends MazeGenerator<MazeGenerator2D.BlockChecker2D> {

    protected MazeGenerator2D(MazeGeneratorConfig config) {
        super(config);
    }

    public void generateChunk(WorldGenLevel world, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        long worldSeed = world.getSeed();
        int xs = chunkPos.getMinBlockX();
        int zs = chunkPos.getMinBlockZ();
        int xe = chunkPos.getMaxBlockX();
        int ze = chunkPos.getMaxBlockZ();

        BlockChecker2D blockChecker = this.getBlockChecker(worldSeed);

        int wallTopY = world.getMaxY();

        BlockState defaultState = this.getWallBlockState(world);

        for(int i = xs; i <= xe; i++) {
            for(int j = zs; j <= ze; j++) {
                if(blockChecker.isBlockAt(i, j))
                    placeColumn(world, chunk, i, j, wallTopY, defaultState);
            }
        }
        clearBlockEntities(chunk, defaultState.getBlock());
    }

    protected static void placeColumn(WorldGenLevel world, ChunkAccess chunk, int cx, int cz, int top, BlockState blockState) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(cx, world.getMinY(), cz);
        while(pos.getY() <= top) {
            chunk.setBlockState(pos, blockState);
            pos.setY(pos.getY()+1);
        }
    }

    @FunctionalInterface
    public interface BlockChecker2D {
        boolean isBlockAt(int x, int z);
    }
}
