package net.replaceitem.mazeworld;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.replaceitem.mazeworld.types.SimplexNoise3DMazeGenerator;

public abstract class MazeGenerator3D extends MazeGenerator<MazeGenerator3D.BlockChecker3D> {
    protected MazeGenerator3D(MazeChunkGeneratorConfig config) {
        super(config);
    }

    @Override
    public void generateChunk(WorldGenLevel world, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        long worldSeed = world.getSeed();
        int xs = chunkPos.getMinBlockX();
        int ys = world.getMinY();
        int zs = chunkPos.getMinBlockZ();

        int xe = chunkPos.getMaxBlockX();
        int ye = world.getMaxY();
        int ze = chunkPos.getMaxBlockZ();

        SimplexNoise3DMazeGenerator.BlockChecker3D blockChecker = getBlockChecker(worldSeed);

        BlockState defaultState = this.getWallBlockState(world);

        for(int x = xs; x <= xe; x++) {
            for(int y = ys; y <= ye; y++) {
                for (int z = zs; z <= ze; z++) {
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
