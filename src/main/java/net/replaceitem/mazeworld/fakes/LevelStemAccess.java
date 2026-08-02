package net.replaceitem.mazeworld.fakes;

import net.minecraft.core.Holder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.replaceitem.mazeworld.config.MazeGeneratorConfig;
import net.replaceitem.mazeworld.RecordRecoderRegistration;
import org.jspecify.annotations.Nullable;

public interface LevelStemAccess {
    @Nullable MazeGeneratorConfig getMazeGenerator();
    LevelStem withMazeGenerator(@Nullable MazeGeneratorConfig mazeGenerator);

    static LevelStem createWithMazeGenerator(Holder<DimensionType> type, ChunkGenerator generator, @Nullable MazeGeneratorConfig mazeGenerator) {
        if(mazeGenerator != null) {
            RecordRecoderRegistration.LEVEL_STEM_MAZE_GENERATOR_KEY.queueNext(mazeGenerator);
        }
        return new LevelStem(type, generator);
    }
}
