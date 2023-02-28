package net.replaceitem.mazeworld.types;

import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator2D;

public class BinaryTreeMazeGenerator extends MazeGenerator2D {
    public BinaryTreeMazeGenerator(MazeChunkGeneratorConfig config) {
        super(config);
    }

    @Override
    public BlockChecker2D getBlockChecker(long seed) {
        int spacing = config.spacing;
        double threshold = config.threshold;
        return (x, z) -> {
            int tx = Math.floorDiv(x, spacing);
            int tz = Math.floorDiv(z, spacing);
            boolean wallDirection = getRandomIntAt(tx, tz, seed, 1000) >= threshold*1000; // true=wall in x, false=wall in z
            return wallDirection ? Math.floorDiv(z - 1, spacing) != tz : Math.floorDiv(x - 1, spacing) != tx;
        };
    }
}
