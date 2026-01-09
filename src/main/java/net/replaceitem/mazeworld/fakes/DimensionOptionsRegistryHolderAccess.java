package net.replaceitem.mazeworld.fakes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;

public interface DimensionOptionsRegistryHolderAccess {
    WorldDimensions globalWith(RegistryAccess dynamicRegistryManager, MazeChunkGeneratorConfig chunkGenerator);
}
