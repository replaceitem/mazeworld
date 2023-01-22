package net.replaceitem.mazeworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MazeChunkGeneratorConfig {

    public static final Codec<MazeChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("spacing").orElse(5).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.spacing),
            Codec.STRING.fieldOf("maze_type").orElse(MazeTypes.BINARY_TREE.id).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.mazeType.id),
            Codec.BOOL.fieldOf("infinite_wall").orElse(true).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.infiniteWall)
    ).apply(instance, MazeChunkGeneratorConfig::new));

    public MazeChunkGeneratorConfig(int spacing, MazeType mazeTypeId, boolean infiniteWall) {
        this.spacing = spacing;
        this.mazeType = mazeTypeId;
        this.infiniteWall = infiniteWall;
    }

    public MazeChunkGeneratorConfig(int spacing, String mazeTypeId, boolean infiniteWall) {
        this(spacing, MazeTypes.byId.get(mazeTypeId), infiniteWall);
    }
    
    public static MazeChunkGeneratorConfig getDefaultConfig() {
        return new MazeChunkGeneratorConfig(5, MazeTypes.BINARY_TREE.id, true);
    }
    
    public int spacing;
    public MazeType mazeType;
    public boolean infiniteWall;
    
    public MazeChunkGeneratorConfig copy() {
        return new MazeChunkGeneratorConfig(this.spacing, this.mazeType, this.infiniteWall);
    }
}
