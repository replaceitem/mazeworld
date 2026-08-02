package net.replaceitem.mazeworld.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.replaceitem.mazeworld.config.MazeGeneratorConfig;

public abstract class MazeGenerator2D extends MazeGenerator<MazeGenerator2D.BlockChecker2D> {

    protected MazeGenerator2D(MazeGeneratorConfig config) {
        super(config);
    }

    public void generateChunk(WorldGenLevel world, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        long worldSeed = world.getSeed();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int minY = Math.max(world.getMinY(), mazeGeneratorConfig.minY());
        int maxY = Math.min(world.getMaxY(), mazeGeneratorConfig.maxY() - 1);
        int maxX = chunkPos.getMaxBlockX();
        int maxZ = chunkPos.getMaxBlockZ();

        BlockChecker2D blockChecker = this.getBlockChecker(worldSeed);
        BlockState defaultState = this.getWallBlockState(world);

        for(int i = minX; i <= maxX; i++) {
            for(int j = minZ; j <= maxZ; j++) {
                if(blockChecker.isBlockAt(i, j)) {
                    placeColumn(chunk, i, j, minY, maxY, defaultState);
                }
            }
        }
        clearBlockEntities(chunk, defaultState.getBlock());
    }

    protected void placeColumn(ChunkAccess chunk, int x, int z, int minY, int maxY, BlockState blockState) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, minY, z);
        while(pos.getY() <= maxY) {
            chunk.setBlockState(pos, blockState);
            pos.setY(pos.getY()+1);
        }
    }

    @FunctionalInterface
    public interface BlockChecker2D {
        boolean isBlockAt(int x, int z);
    }
}
