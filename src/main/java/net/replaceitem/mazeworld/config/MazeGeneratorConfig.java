package net.replaceitem.mazeworld.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.replaceitem.mazeworld.generator.MazeGenerator;

public record MazeGeneratorConfig(
        boolean infiniteWall,
        Identifier wallBlock,
        int minY,
        int maxY,
        MazeType mazeType
) {
    public static final Codec<MazeGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("infinite_wall").orElse(false).forGetter(MazeGeneratorConfig::infiniteWall),
            Identifier.CODEC.fieldOf("maze_block").forGetter(MazeGeneratorConfig::wallBlock),
            Codec.INT.fieldOf("min_y").orElse(Integer.MIN_VALUE).forGetter(MazeGeneratorConfig::minY),
            Codec.INT.fieldOf("max_y").orElse(Integer.MAX_VALUE).forGetter(MazeGeneratorConfig::maxY),
            MazeType.CODEC.fieldOf("maze_type").forGetter(MazeGeneratorConfig::mazeType)
    ).apply(instance, MazeGeneratorConfig::new));

    public MazeGenerator<?> createGenerator() {
        return this.mazeType.createGenerator(this);
    }
}
