package net.replaceitem.mazeworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.replaceitem.mazeworld.generator.MazeGenerator;
import net.replaceitem.mazeworld.types.MazeType;

public record MazeGeneratorConfig(
        boolean infiniteWall,
        Identifier wallBlock,
        MazeType mazeType
) {
    public static final Codec<MazeGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("infinite_wall").forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.infiniteWall),
            Identifier.CODEC.fieldOf("maze_block").forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.wallBlock),
            MazeType.CODEC.fieldOf("maze_type").forGetter(mazeGeneratorConfig -> mazeGeneratorConfig.mazeType)
    ).apply(instance, MazeGeneratorConfig::new));

    public MazeGenerator<?> createGenerator() {
        return this.mazeType.createGenerator(this);
    }
}
