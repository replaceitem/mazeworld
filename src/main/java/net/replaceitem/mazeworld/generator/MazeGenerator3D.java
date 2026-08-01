package net.replaceitem.mazeworld.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.replaceitem.mazeworld.MazeGeneratorConfig;

public abstract class MazeGenerator3D extends MazeGenerator<MazeGenerator3D.BlockChecker3D> {
    protected MazeGenerator3D(MazeGeneratorConfig config) {
        super(config);
    }

    @Override
    public void generateChunk(WorldGenLevel world, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        long worldSeed = world.getSeed();

        int minX = chunkPos.getMinBlockX();
        int minY = Math.max(world.getMinY(), mazeGeneratorConfig.minY());
        int minZ = chunkPos.getMinBlockZ();

        int maxX = chunkPos.getMaxBlockX();
        int maxY = Math.min(world.getMaxY(), mazeGeneratorConfig.maxY() - 1);
        int maxZ = chunkPos.getMaxBlockZ();


        SimplexNoise3DMazeGenerator.BlockChecker3D blockChecker = getBlockChecker(worldSeed);

        BlockState defaultState = this.getWallBlockState(world);

        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (blockChecker.isBlockAt(x, y, z)) {
                        chunk.setBlockState(new BlockPos(x, y, z), defaultState);
                    }
                }
            }
        }
        clearBlockEntities(chunk, defaultState.getBlock());
    }
    
    public interface BlockChecker3D extends MazeGenerator2D.BlockChecker2D {
        default boolean isBlockAt(int x, int z) {
            return isBlockAt(x, 0, z);
        }
        boolean isBlockAt(int x, int y, int z);
    }
}
