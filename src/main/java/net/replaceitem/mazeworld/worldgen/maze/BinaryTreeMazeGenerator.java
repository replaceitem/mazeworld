package net.replaceitem.mazeworld.worldgen.maze;

public class BinaryTreeMazeGenerator extends MazeGenerator {
    private final int spacing;
    private final float bias;

    public BinaryTreeMazeGenerator(int spacing, float bias) {
        this.spacing = spacing;
        this.bias = bias;
    }

    @Override
    public BlockChecker getBlockChecker(long seed) {
        return (x, _, z) -> {
            int tx = Math.floorDiv(x, spacing);
            int tz = Math.floorDiv(z, spacing);
            boolean wallDirection = getRandomIntAt(tx, tz, seed, 1000) >= bias*1000; // true=wall in x, false=wall in z
            return wallDirection ? Math.floorDiv(z - 1, spacing) != tz : Math.floorDiv(x - 1, spacing) != tx;
        };
    }
}
