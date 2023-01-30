package net.replaceitem.mazeworld.types;

import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator;
import net.minecraft.util.math.ChunkPos;

public class BinaryTreeMazeGenerator extends MazeGenerator {
    public BinaryTreeMazeGenerator(MazeChunkGeneratorConfig config) {
        super(config);
    }

    @Override
    public BlockChecker getBlockChecker(ChunkPos chunkPos, long seed) {
        int spacing = config.spacing;
        double threshold = config.threshold;
        return (x, y) -> {
            int tx = Math.floorDiv(x, spacing);
            int tz = Math.floorDiv(y, spacing);
            boolean wallDirection = getRandomIntAt(tx, tz, seed, 1000) >= threshold*1000; // true=wall in x, false=wall in z
            return wallDirection ? Math.floorDiv(y - 1, spacing) != tz : Math.floorDiv(x - 1, spacing) != tx;
        };
    }
}
