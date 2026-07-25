package net.replaceitem.mazeworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Objects;

@Deprecated()
public class LegacyMazeChunkGeneratorConfig {

    public static final Identifier BEDROCK_IDENTIFIER = Identifier.fromNamespaceAndPath("minecraft","bedrock");

    public static final Codec<LegacyMazeChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("spacing").orElse(5).forGetter(legacyMazeChunkGeneratorConfig -> legacyMazeChunkGeneratorConfig.spacing),
            Codec.STRING.fieldOf("maze_type").orElse(MazeTypes.BINARY_TREE.identifier().toString()).forGetter(legacyMazeChunkGeneratorConfig -> Objects.requireNonNull(MazeWorld.MAZE_TYPE_REGISTRY.getKey(legacyMazeChunkGeneratorConfig.mazeType)).getPath()),
            Codec.BOOL.fieldOf("infinite_wall").orElse(true).forGetter(legacyMazeChunkGeneratorConfig -> legacyMazeChunkGeneratorConfig.infiniteWall),
            Codec.DOUBLE.fieldOf("threshold").orElse(0.5).forGetter(legacyMazeChunkGeneratorConfig -> legacyMazeChunkGeneratorConfig.threshold),
            Codec.STRING.fieldOf("maze_block").orElse(BEDROCK_IDENTIFIER.toString()).forGetter(legacyMazeChunkGeneratorConfig -> legacyMazeChunkGeneratorConfig.wallBlock.toString())
    ).apply(instance, LegacyMazeChunkGeneratorConfig::new));

    public LegacyMazeChunkGeneratorConfig(int spacing, MazeType mazeType, boolean infiniteWall, double threshold, Identifier wallBlock) {
        this.spacing = spacing;
        this.mazeType = mazeType;
        this.infiniteWall = infiniteWall;
        this.threshold = threshold;
        this.wallBlock = wallBlock;
    }

    public LegacyMazeChunkGeneratorConfig(int spacing, String mazeTypeId, boolean infiniteWall, double threshold, String wallBlockIdentifier) {
        this(spacing, MazeWorld.MAZE_TYPE_REGISTRY.getValueOrThrow(ResourceKey.create(MazeWorld.MAZE_TYPE_REGISTRY_KEY, MazeWorld.id(mazeTypeId))), infiniteWall, threshold, Identifier.parse(wallBlockIdentifier));
    }
    
    public int spacing;
    public MazeType mazeType;
    public boolean infiniteWall;
    public double threshold;
    public Identifier wallBlock;
}
