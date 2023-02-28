package net.replaceitem.mazeworld.types;

import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator3D;

public class SimplexNoise3DMazeGenerator extends MazeGenerator3D {
    public SimplexNoise3DMazeGenerator(MazeChunkGeneratorConfig config) {
        super(config);
    }

    @Override
    public BlockChecker3D getBlockChecker(long seed) {
        SimplexNoiseSampler simplexNoiseSampler = new SimplexNoiseSampler(Random.create(seed));
        double spacing = config.spacing;
        double threshold = config.threshold*2-1;
        return (x, y, z) -> {
            double sample = simplexNoiseSampler.sample(x / spacing, y / spacing, z / spacing);
            return sample >= threshold;
        };
    }
}
