package net.replaceitem.mazeworld;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.replaceitem.mazeworld.types.SimplexNoise3DMazeGenerator;

public abstract class MazeGenerator3D extends MazeGenerator<MazeGenerator3D.BlockChecker3D> {
    protected MazeGenerator3D(MazeChunkGeneratorConfig config) {
        super(config);
    }

    @Override
    public void generateChunk(StructureWorldAccess world, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        long worldSeed = world.getSeed();
        int xs = chunkPos.getStartX();
        int ys = world.getBottomY();
        int zs = chunkPos.getStartZ();

        int xe = chunkPos.getEndX();
        int ye = world.getTopY()-1;
        int ze = chunkPos.getEndZ();

        SimplexNoise3DMazeGenerator.BlockChecker3D blockChecker = getBlockChecker(worldSeed);

        for(int x = xs; x <= xe; x++) {
            for(int y = ys; y <= ye; y++) {
                for (int z = zs; z <= ze; z++) {
                    if (blockChecker.isBlockAt(x, y, z)) {
                        chunk.setBlockState(new BlockPos(x, y, z), Blocks.BEDROCK.getDefaultState(), false);
                    }
                }
            }
        }
        clearBlockEntities(chunk);
    }
    
    public interface BlockChecker3D extends MazeGenerator2D.BlockChecker2D {
        default boolean isBlockAt(int x, int z) {
            return isBlockAt(x, 0, z);
        }
        boolean isBlockAt(int x, int y, int z);

    }
}
