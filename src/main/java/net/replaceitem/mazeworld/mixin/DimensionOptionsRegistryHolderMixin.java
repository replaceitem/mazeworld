package net.replaceitem.mazeworld.mixin;

import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.replaceitem.mazeworld.MazeChunkGenerator;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.fakes.DimensionOptionsRegistryHolderAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Objects;

// Note to future me - NEVER touch this again
@Mixin(DimensionOptionsRegistryHolder.class)
public abstract class DimensionOptionsRegistryHolderMixin implements DimensionOptionsRegistryHolderAccess {
    @Shadow @Final private Registry<DimensionOptions> dimensions;

    /**
     * Replica of with(), except applied to all dimensions
     */
    @Override
    public DimensionOptionsRegistryHolder globalWith(DynamicRegistryManager dynamicRegistryManager, MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        Registry<DimensionType> registry = dynamicRegistryManager.get(RegistryKeys.DIMENSION_TYPE);
        Registry<DimensionOptions> registry2 = createGlobalRegistry(registry, this.dimensions, mazeChunkGeneratorConfig);
        return new DimensionOptionsRegistryHolder(registry2);
    }

    private static RegistryEntry<DimensionType> getEntry(Registry<DimensionType> dynamicRegistry, Registry<DimensionOptions> currentRegistry, RegistryKey<DimensionOptions> dimensionOptionsRegistryKey, RegistryKey<DimensionType> dimensionTypeRegistryKey) {
        DimensionOptions dimensionOptions = currentRegistry.get(dimensionOptionsRegistryKey);
        return dimensionOptions == null ? dynamicRegistry.entryOf(dimensionTypeRegistryKey) : dimensionOptions.dimensionTypeEntry();
    }

    private static Registry<DimensionOptions> createGlobalRegistry(Registry<DimensionType> dynamicRegistry, Registry<DimensionOptions> currentRegistry, MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(RegistryKeys.DIMENSION, Lifecycle.experimental());

        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : currentRegistry.getEntrySet()) {
            RegistryKey<DimensionOptions> registryKey = entry.getKey();
            DimensionOptions dimensionOptions = entry.getValue();

            NoiseChunkGenerator currentChunkGenerator = (NoiseChunkGenerator) Objects.requireNonNull(dimensionOptions).chunkGenerator();
            MazeChunkGenerator generator = new MazeChunkGenerator(currentChunkGenerator.getBiomeSource(), currentChunkGenerator.getSettings(), mazeChunkGeneratorConfig);

            mutableRegistry.add(registryKey, new DimensionOptions(getEntry(dynamicRegistry, currentRegistry, registryKey, dimensionOptions.dimensionTypeEntry().getKey().orElseThrow()), generator), currentRegistry.getEntryLifecycle(entry.getValue()));
        }
        return mutableRegistry.freeze();
    }
}
