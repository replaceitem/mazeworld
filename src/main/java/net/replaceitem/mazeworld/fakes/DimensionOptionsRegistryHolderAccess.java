package net.replaceitem.mazeworld.fakes;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;

public interface DimensionOptionsRegistryHolderAccess {
    DimensionOptionsRegistryHolder globalWith(DynamicRegistryManager dynamicRegistryManager, MazeChunkGeneratorConfig chunkGenerator);
}
