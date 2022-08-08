package mazeworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MazeChunkGeneratorConfig {

    public static final Codec<MazeChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("spacing").orElse(5).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.spacing),
            Codec.STRING.fieldOf("maze_type").orElse(MazeTypes.BINARY_TREE.id).forGetter(mazeChunkGeneratorConfig -> mazeChunkGeneratorConfig.mazeType.id)
    ).apply(instance, MazeChunkGeneratorConfig::new));

    public MazeChunkGeneratorConfig(int spacing, MazeType mazeTypeId) {
        this.spacing = spacing;
        this.mazeType = mazeTypeId;
    }

    public MazeChunkGeneratorConfig(int spacing, String mazeTypeId) {
        this(spacing, MazeTypes.byId.get(mazeTypeId));
    }
    
    public static MazeChunkGeneratorConfig getDefaultConfig() {
        return new MazeChunkGeneratorConfig(5, MazeTypes.BINARY_TREE.id);
    }
    
    public int spacing;
    public MazeType mazeType;
    
    public MazeChunkGeneratorConfig copy() {
        return new MazeChunkGeneratorConfig(this.spacing, this.mazeType);
    }
}
