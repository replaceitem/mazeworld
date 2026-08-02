package net.replaceitem.mazeworld;

import net.replaceitem.mazeworld.config.MazeGeneratorConfig;
import net.replaceitem.mazeworld.worldgen.MazeChunkGenerator;
import org.jspecify.annotations.Nullable;
import recordrecoder.api.record.ComponentKeyRegistry;
import recordrecoder.api.record.RecordComponentKey;

public class RecordRecoderRegistration implements Runnable {
    public static final RecordComponentKey<@Nullable MazeGeneratorConfig> LEVEL_STEM_MAZE_GENERATOR_KEY = RecordComponentKey.create(
            "maze_generator", "net/minecraft/world/level/dimension/LevelStem",
            MazeGeneratorConfig.class, ((MazeGeneratorConfig) null)
    );
    public static final RecordComponentKey<@Nullable MazeChunkGenerator> WORLD_GEN_CONTEXT_MAZE_GENERATOR = RecordComponentKey.create(
            "maze_generator", "net/minecraft/world/level/chunk/status/WorldGenContext",
            MazeChunkGenerator.class, ((MazeChunkGenerator) null)
    );

    @Override
    public void run() {
        ComponentKeyRegistry.INSTANCE.register(RecordRecoderRegistration.LEVEL_STEM_MAZE_GENERATOR_KEY);
        ComponentKeyRegistry.INSTANCE.register(RecordRecoderRegistration.WORLD_GEN_CONTEXT_MAZE_GENERATOR);
    }
}
