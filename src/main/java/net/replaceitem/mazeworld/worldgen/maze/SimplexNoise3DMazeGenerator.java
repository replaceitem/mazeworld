package net.replaceitem.mazeworld.worldgen.maze;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class SimplexNoise3DMazeGenerator extends MazeGenerator {
    private final double size;
    private final double threshold;

    public SimplexNoise3DMazeGenerator(double size, double threshold) {
        this.size = size;
        this.threshold = threshold;
    }

    public BlockChecker getBlockChecker(long seed) {
        SimplexNoise simplexNoiseSampler = new SimplexNoise(RandomSource.create(seed));
        double t = threshold * 2 - 1;
        return (x, y, z) -> {
            double sample = simplexNoiseSampler.get(x / size, y / size, z / size);
            return sample >= t;
        };
    }

    @Override
    public boolean is3d() {
        return true;
    }
}
