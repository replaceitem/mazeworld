package net.replaceitem.mazeworld.generator;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.replaceitem.mazeworld.MazeGeneratorConfig;

public class SimplexNoise3DMazeGenerator extends MazeGenerator3D {
    private final double size;
    private final double threshold;

    public SimplexNoise3DMazeGenerator(MazeGeneratorConfig config, double size, double threshold) {
        super(config);
        this.size = size;
        this.threshold = threshold;
    }

    @Override
    public BlockChecker3D getBlockChecker(long seed) {
        SimplexNoise simplexNoiseSampler = new SimplexNoise(RandomSource.create(seed));
        double t = threshold * 2 - 1;
        return (x, y, z) -> {
            double sample = simplexNoiseSampler.getValue(x / size, y / size, z / size);
            return sample >= t;
        };
    }
}
