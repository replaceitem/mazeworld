package net.replaceitem.mazeworld.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.replaceitem.mazeworld.worldgen.maze.MazeGenerator;

import java.util.function.Predicate;

public class MazePlacer {
    private Predicate<BlockPos> condition = (_) -> true;
    private final ChunkAccess chunk;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    public MazePlacer(ChunkAccess chunk, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.chunk = chunk;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void addCondition(Predicate<BlockPos> predicate) {
        this.condition = this.condition.and(predicate);
    }

    public void generate3d(MazeGenerator.BlockChecker blockChecker, BlockState state) {
        var pos = new BlockPos.MutableBlockPos();
        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (blockChecker.isBlockAt(x, y, z)) {
                        pos.set(x, y, z);
                        if(condition.test(pos)) chunk.setBlockState(pos, state);
                    }
                }
            }
        }
    }

    public void generate2d(MazeGenerator.BlockChecker blockChecker, BlockState state) {
        var pos = new BlockPos.MutableBlockPos();
        for(int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (blockChecker.isBlockAt(x, 0, z)) {
                    for(int y = minY; y <= maxY; y++) {
                        pos.set(x, y, z);
                        if(condition.test(pos)) chunk.setBlockState(pos, state);
                    }
                }
            }
        }
    }
}
