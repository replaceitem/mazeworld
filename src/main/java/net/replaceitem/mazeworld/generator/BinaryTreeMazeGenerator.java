package net.replaceitem.mazeworld.generator;

import net.replaceitem.mazeworld.MazeGeneratorConfig;

public class BinaryTreeMazeGenerator extends MazeGenerator2D {
    private final int spacing;
    private final float bias;

    public BinaryTreeMazeGenerator(MazeGeneratorConfig mazeConfig, int spacing, float bias) {
        super(mazeConfig);
        this.spacing = spacing;
        this.bias = bias;
    }

    @Override
    public BlockChecker2D getBlockChecker(long seed) {
        return (x, z) -> {
            int tx = Math.floorDiv(x, spacing);
            int tz = Math.floorDiv(z, spacing);
            boolean wallDirection = getRandomIntAt(tx, tz, seed, 1000) >= bias*1000; // true=wall in x, false=wall in z
            return wallDirection ? Math.floorDiv(z - 1, spacing) != tz : Math.floorDiv(x - 1, spacing) != tx;
        };
    }
}
