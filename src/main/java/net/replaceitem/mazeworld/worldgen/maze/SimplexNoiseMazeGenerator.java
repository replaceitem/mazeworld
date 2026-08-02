package net.replaceitem.mazeworld.worldgen.maze;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class SimplexNoiseMazeGenerator extends MazeGenerator {
    private final double size;
    private final double threshold;

    public SimplexNoiseMazeGenerator(double size, double threshold) {
        this.size = size;
        this.threshold = threshold;
    }

    @Override
    public BlockChecker getBlockChecker(long seed) {
        SimplexNoise simplexNoiseSampler = new SimplexNoise(RandomSource.create(seed));
        double t = threshold * 2 - 1;
        return (x, _, z) -> {
            double sample = simplexNoiseSampler.getValue(x / size, z / size);
            return sample >= t;
        };
    }
}
