package net.replaceitem.mazeworld.level;

import net.minecraft.world.level.block.Block;

public record InfiniteWallConfig(int minY, int maxY, Block mazeWallBlock) {
}