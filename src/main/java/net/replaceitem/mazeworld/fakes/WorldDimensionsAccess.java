package net.replaceitem.mazeworld.fakes;

import net.minecraft.world.level.levelgen.WorldDimensions;
import net.replaceitem.mazeworld.config.MazeGeneratorConfig;
import org.jspecify.annotations.Nullable;

public interface WorldDimensionsAccess {
    WorldDimensions withMazeGenerator(@Nullable MazeGeneratorConfig mazeGenerator);
}
