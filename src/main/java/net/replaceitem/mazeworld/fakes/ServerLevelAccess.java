package net.replaceitem.mazeworld.fakes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.LevelStem;

public interface ServerLevelAccess {
    ScopedValue<LevelStem> LEVEL_STEM = ScopedValue.newInstance();

    boolean isInfiniteMaze();
    Block getMazeWallBlock();
}
