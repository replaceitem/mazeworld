package net.replaceitem.mazeworld.types;

import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeType;
import net.minecraft.util.math.ChunkPos;

public class BinaryTreeMazeType extends MazeType {
    public BinaryTreeMazeType() {
        super("binary_tree");
    }

    @Override
    public BlockChecker getBlockChecker(ChunkPos chunkPos, MazeChunkGeneratorConfig config, long seed) {
        int spacing = config.spacing;
        return (x, y) -> {
            int tx = Math.floorDiv(x, spacing);
            int tz = Math.floorDiv(y, spacing);
            boolean wallDirection = getRandomIntAt(tx, tz, seed, 2) == 0; // true=wall in x, false=wall in z
            return wallDirection ? Math.floorDiv(y - 1, spacing) != tz : Math.floorDiv(x - 1, spacing) != tx;
        };
    }
}
