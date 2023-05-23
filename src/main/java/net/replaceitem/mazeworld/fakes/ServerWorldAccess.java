package net.replaceitem.mazeworld.fakes;

import net.minecraft.block.Block;

public interface ServerWorldAccess {
    boolean isInfiniteMaze();
    Block getMazeWallBlock();
}
