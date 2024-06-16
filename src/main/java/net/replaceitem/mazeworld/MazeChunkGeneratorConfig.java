package net.replaceitem.mazeworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public class MazeChunkGeneratorConfig {

    public static final Identifier BEDROCK_IDENTIFIER = Identifier.of("minecraft","bedrock");

    public static final Codec<MazeChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("spacing").orElse(5).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.spacing),
            Codec.STRING.fieldOf("maze_type").orElse(MazeTypes.BINARY_TREE.id).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.mazeType.id),
            Codec.BOOL.fieldOf("infinite_wall").orElse(true).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.infiniteWall),
            Codec.DOUBLE.fieldOf("threshold").orElse(0.5).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.threshold),
            Codec.STRING.fieldOf("maze_block").orElse(BEDROCK_IDENTIFIER.toString()).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.wallBlock.toString())
    ).apply(instance, MazeChunkGeneratorConfig::new));

    public MazeChunkGeneratorConfig(int spacing, MazeType mazeType, boolean infiniteWall, double threshold, Identifier wallBlock) {
        if(mazeType == null) mazeType = MazeTypes.BINARY_TREE;
        if(wallBlock == null) wallBlock = BEDROCK_IDENTIFIER;
        this.spacing = spacing;
        this.mazeType = mazeType;
        this.infiniteWall = infiniteWall;
        this.threshold = threshold;
        this.wallBlock = wallBlock;
    }

    public MazeChunkGeneratorConfig(int spacing, String mazeTypeId, boolean infiniteWall, double threshold, String wallBlockIdentifier) {
        this(spacing, MazeTypes.byId.get(mazeTypeId), infiniteWall, threshold, Identifier.tryParse(wallBlockIdentifier));
    }
    
    public static MazeChunkGeneratorConfig getDefaultConfig() {
        return new MazeChunkGeneratorConfig(5, MazeTypes.BINARY_TREE, true, 0.5, BEDROCK_IDENTIFIER);
    }
    
    public int spacing;
    public MazeType mazeType;
    public boolean infiniteWall;
    public double threshold;
    public Identifier wallBlock;
    
    public MazeChunkGeneratorConfig copy() {
        return new MazeChunkGeneratorConfig(this.spacing, this.mazeType, this.infiniteWall, this.threshold, this.wallBlock);
    }
}
