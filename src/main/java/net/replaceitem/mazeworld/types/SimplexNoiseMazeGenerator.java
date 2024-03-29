package net.replaceitem.mazeworld.types;

import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator2D;

public class SimplexNoiseMazeGenerator extends MazeGenerator2D {
    public SimplexNoiseMazeGenerator(MazeChunkGeneratorConfig config) {
        super(config);
    }

    @Override
    public BlockChecker2D getBlockChecker(long seed) {
        SimplexNoiseSampler simplexNoiseSampler = new SimplexNoiseSampler(Random.create(seed));
        double spacing = config.spacing;
        double threshold = config.threshold*2-1;
        return (x, z) -> {
            double sample = simplexNoiseSampler.sample(x / spacing, z / spacing);
            return sample >= threshold;
        };
    }
}
