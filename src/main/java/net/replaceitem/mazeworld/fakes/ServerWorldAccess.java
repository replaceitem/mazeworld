package net.replaceitem.mazeworld.fakes;

import net.minecraft.world.level.block.Block;

public interface ServerWorldAccess {
    boolean isInfiniteMaze();
    Block getMazeWallBlock();
}
