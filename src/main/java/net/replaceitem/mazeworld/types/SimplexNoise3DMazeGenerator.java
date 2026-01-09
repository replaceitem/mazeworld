package net.replaceitem.mazeworld.types;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator3D;

public class SimplexNoise3DMazeGenerator extends MazeGenerator3D {
    public SimplexNoise3DMazeGenerator(MazeChunkGeneratorConfig config) {
        super(config);
    }

    @Override
    public BlockChecker3D getBlockChecker(long seed) {
        SimplexNoise simplexNoiseSampler = new SimplexNoise(RandomSource.create(seed));
        double spacing = config.spacing;
        double threshold = config.threshold*2-1;
        return (x, y, z) -> {
            double sample = simplexNoiseSampler.getValue(x / spacing, y / spacing, z / spacing);
            return sample >= threshold;
        };
    }
}
