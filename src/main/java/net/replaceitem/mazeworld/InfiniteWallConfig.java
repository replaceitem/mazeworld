package net.replaceitem.mazeworld;

import net.minecraft.world.level.block.Block;

public record InfiniteWallConfig(int minY, int maxY, Block mazeWallBlock) {
}