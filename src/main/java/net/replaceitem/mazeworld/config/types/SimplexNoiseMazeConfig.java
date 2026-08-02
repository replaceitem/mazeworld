package net.replaceitem.mazeworld.config.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.replaceitem.mazeworld.config.MazeGeneratorConfig;
import net.replaceitem.mazeworld.config.MazeType;
import net.replaceitem.mazeworld.generator.MazeGenerator;
import net.replaceitem.mazeworld.generator.SimplexNoiseMazeGenerator;

public record SimplexNoiseMazeConfig(double size, double threshold) implements MazeType {
    public static final MapCodec<SimplexNoiseMazeConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("size").forGetter(SimplexNoiseMazeConfig::size),
            Codec.DOUBLE.fieldOf("threshold").forGetter(SimplexNoiseMazeConfig::threshold)
    ).apply(instance, SimplexNoiseMazeConfig::new));

    @Override
    public MapCodec<? extends MazeType> codec() {
        return CODEC;
    }

    @Override
    public MazeGenerator<?> createGenerator(MazeGeneratorConfig mazeGeneratorConfig) {
        return new SimplexNoiseMazeGenerator(mazeGeneratorConfig, size, threshold);
    }
}
