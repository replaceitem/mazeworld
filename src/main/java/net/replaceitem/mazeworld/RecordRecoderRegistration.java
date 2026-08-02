package net.replaceitem.mazeworld;

import net.replaceitem.mazeworld.config.MazeGeneratorConfig;
import net.replaceitem.mazeworld.generator.MazeGenerator;
import org.jspecify.annotations.Nullable;
import recordrecoder.api.record.ComponentKeyRegistry;
import recordrecoder.api.record.RecordComponentKey;

public class RecordRecoderRegistration implements Runnable {
    public static final RecordComponentKey<@Nullable MazeGeneratorConfig> LEVEL_STEM_MAZE_GENERATOR_KEY = RecordComponentKey.create(
            "maze_generator", "net/minecraft/world/level/dimension/LevelStem",
            MazeGeneratorConfig.class, ((MazeGeneratorConfig) null)
    );
    @SuppressWarnings("rawtypes")
    public static final RecordComponentKey<@Nullable MazeGenerator> WORLD_GEN_CONTEXT_MAZE_GENERATOR = RecordComponentKey.create(
            "maze_generator", "net/minecraft/world/level/chunk/status/WorldGenContext",
            MazeGenerator.class, ((MazeGenerator<?>) null)
    );

    @Override
    public void run() {
        ComponentKeyRegistry.INSTANCE.register(RecordRecoderRegistration.LEVEL_STEM_MAZE_GENERATOR_KEY);
        ComponentKeyRegistry.INSTANCE.register(RecordRecoderRegistration.WORLD_GEN_CONTEXT_MAZE_GENERATOR);
    }
}
