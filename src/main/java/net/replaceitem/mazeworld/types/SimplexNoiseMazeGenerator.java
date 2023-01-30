package net.replaceitem.mazeworld.types;

import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator;

public class SimplexNoiseMazeGenerator extends MazeGenerator {
    public SimplexNoiseMazeGenerator(MazeChunkGeneratorConfig config) {
        super(config);
    }

    @Override
    public BlockChecker getBlockChecker(long seed) {
        SimplexNoiseSampler simplexNoiseSampler = new SimplexNoiseSampler(Random.create(seed));
        double spacing = config.spacing;
        double threshold = config.threshold*2-1;
        return (x, y) -> {
            double sample = simplexNoiseSampler.sample(x / spacing, y / spacing);
            return sample >= threshold;
        };
    }
}
