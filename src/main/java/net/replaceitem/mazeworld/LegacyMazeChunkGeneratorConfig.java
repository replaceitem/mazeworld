package net.replaceitem.mazeworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.replaceitem.mazeworld.types.*;

@Deprecated()
public class LegacyMazeChunkGeneratorConfig {
    public static final Identifier BEDROCK_IDENTIFIER = Identifier.fromNamespaceAndPath("minecraft","bedrock");

    private final int spacing;
    private final String mazeType;
    private final boolean infiniteWall;
    private final double threshold;
    private final String wallBlock;

    private final MazeGeneratorConfig migratedConfig;

    public static final Codec<LegacyMazeChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("spacing").orElse(5).forGetter(legacyMazeChunkGeneratorConfig -> legacyMazeChunkGeneratorConfig.spacing),
            Codec.STRING.fieldOf("maze_type").orElse(MazeTypes.BINARY_TREE.identifier().toString()).forGetter(legacyMazeChunkGeneratorConfig -> legacyMazeChunkGeneratorConfig.mazeType),
            Codec.BOOL.fieldOf("infinite_wall").orElse(true).forGetter(legacyMazeChunkGeneratorConfig -> legacyMazeChunkGeneratorConfig.infiniteWall),
            Codec.DOUBLE.fieldOf("threshold").orElse(0.5).forGetter(legacyMazeChunkGeneratorConfig -> legacyMazeChunkGeneratorConfig.threshold),
            Codec.STRING.fieldOf("maze_block").orElse(BEDROCK_IDENTIFIER.toString()).forGetter(legacyMazeChunkGeneratorConfig -> legacyMazeChunkGeneratorConfig.wallBlock.toString())
    ).apply(instance, LegacyMazeChunkGeneratorConfig::new));

    public LegacyMazeChunkGeneratorConfig(int spacing, String mazeType, boolean infiniteWall, double threshold, String wallBlock) {
        this.spacing = spacing;
        this.mazeType = mazeType;
        this.infiniteWall = infiniteWall;
        this.threshold = threshold;
        this.wallBlock = wallBlock;
        this.migratedConfig = this.migrate();
    }

    private MazeGeneratorConfig migrate() {
        var mazeType = switch (this.mazeType) {
            case "binary_tree" -> new BinaryTreeMazeConfig(spacing, (float) threshold);
            case "wang_tiles" -> new RectangularWangTilesMazeConfig(spacing, (float) threshold);
            case "round_wang_tiles" -> new RoundWangTilesMazeConfig(spacing, (float) threshold);
            case "simplex_noise" -> new SimplexNoiseMazeConfig(spacing, (float) threshold);
            case "simplex_noise_3d" -> new SimplexNoise3dMazeConfig(spacing, (float) threshold);
            default -> throw new IllegalStateException("Unexpected maze type: " + this.mazeType);
        };
        return new MazeGeneratorConfig(infiniteWall, Identifier.parse(wallBlock), mazeType);
    }

    public MazeGeneratorConfig getMigratedConfig() {
        return migratedConfig;
    }
}
