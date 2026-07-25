package net.replaceitem.mazeworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record MazeGeneratorConfig(
        int spacing,
        MazeType mazeType,
        boolean infiniteWall,
        double threshold,
        Identifier wallBlock
) {
    public static final Identifier BEDROCK_IDENTIFIER = Identifier.fromNamespaceAndPath("minecraft", "bedrock");

    public static final Codec<MazeGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("spacing").orElse(5).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.spacing),
            MazeType.CODEC.fieldOf("maze_type").forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.mazeType),
            Codec.BOOL.fieldOf("infinite_wall").orElse(true).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.infiniteWall),
            Codec.DOUBLE.fieldOf("threshold").orElse(0.5).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.threshold),
            Identifier.CODEC.fieldOf("maze_block").orElse(BEDROCK_IDENTIFIER).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.wallBlock)
    ).apply(instance, MazeGeneratorConfig::new));
}
