package net.replaceitem.mazeworld.types;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator2D;

public class SimplexNoiseMazeGenerator extends MazeGenerator2D {
    public SimplexNoiseMazeGenerator(MazeChunkGeneratorConfig config) {
        super(config);
    }

    @Override
    public BlockChecker2D getBlockChecker(long seed) {
        SimplexNoise simplexNoiseSampler = new SimplexNoise(RandomSource.create(seed));
        double spacing = config.spacing;
        double threshold = config.threshold*2-1;
        return (x, z) -> {
            double sample = simplexNoiseSampler.getValue(x / spacing, z / spacing);
            return sample >= threshold;
        };
    }
}
