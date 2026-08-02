package net.replaceitem.mazeworld.config.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.replaceitem.mazeworld.config.MazeGeneratorConfig;
import net.replaceitem.mazeworld.config.MazeType;
import net.replaceitem.mazeworld.generator.MazeGenerator;
import net.replaceitem.mazeworld.generator.SimplexNoise3DMazeGenerator;

public record SimplexNoise3dMazeConfig(double size, double threshold) implements MazeType {
    public static final MapCodec<SimplexNoise3dMazeConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("size").forGetter(SimplexNoise3dMazeConfig::size),
            Codec.DOUBLE.fieldOf("threshold").forGetter(SimplexNoise3dMazeConfig::threshold)
    ).apply(instance, SimplexNoise3dMazeConfig::new));

    @Override
    public MapCodec<? extends MazeType> codec() {
        return CODEC;
    }

    @Override
    public MazeGenerator<?> createGenerator(MazeGeneratorConfig mazeGeneratorConfig) {
        return new SimplexNoise3DMazeGenerator(mazeGeneratorConfig, size, threshold);
    }
}
