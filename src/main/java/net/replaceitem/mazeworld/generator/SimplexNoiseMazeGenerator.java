package net.replaceitem.mazeworld.generator;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.replaceitem.mazeworld.MazeGeneratorConfig;

public class SimplexNoiseMazeGenerator extends MazeGenerator2D {
    private final double size;
    private final double threshold;

    public SimplexNoiseMazeGenerator(MazeGeneratorConfig config, double size, double threshold) {
        super(config);
        this.size = size;
        this.threshold = threshold;
    }

    @Override
    public BlockChecker2D getBlockChecker(long seed) {
        SimplexNoise simplexNoiseSampler = new SimplexNoise(RandomSource.create(seed));
        double t = threshold * 2 - 1;
        return (x, z) -> {
            double sample = simplexNoiseSampler.getValue(x / size, z / size);
            return sample >= t;
        };
    }
}
