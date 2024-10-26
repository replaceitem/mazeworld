package net.replaceitem.mazeworld.mixin;

import com.google.common.collect.ImmutableMap;
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
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Objects;

// Note to future me - NEVER touch this again
@Mixin(DimensionOptionsRegistryHolder.class)
public abstract class DimensionOptionsRegistryHolderMixin implements DimensionOptionsRegistryHolderAccess {
    @Shadow @Final private Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions;

    /**
     * Replica of with(), except applied to all dimensions
     */
    @Override
    public DimensionOptionsRegistryHolder globalWith(DynamicRegistryManager dynamicRegistryManager, MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        Registry<DimensionType> registry = dynamicRegistryManager.getOrThrow(RegistryKeys.DIMENSION_TYPE);
        Map<RegistryKey<DimensionOptions>, DimensionOptions> registry2 = createGlobalRegistry(registry, this.dimensions, mazeChunkGeneratorConfig);
        return new DimensionOptionsRegistryHolder(registry2);
    }

    @Unique
    private static RegistryEntry<DimensionType> getEntry(Registry<DimensionType> dynamicRegistry, Map<RegistryKey<DimensionOptions>, DimensionOptions> currentRegistry, RegistryKey<DimensionOptions> dimensionOptionsRegistryKey, RegistryKey<DimensionType> dimensionTypeRegistryKey) {
        DimensionOptions dimensionOptions = currentRegistry.get(dimensionOptionsRegistryKey);
        return dimensionOptions == null ? dynamicRegistry.getOrThrow(dimensionTypeRegistryKey) : dimensionOptions.dimensionTypeEntry();
    }

    @Unique
    private static Map<RegistryKey<DimensionOptions>, DimensionOptions> createGlobalRegistry(Registry<DimensionType> dynamicRegistry, Map<RegistryKey<DimensionOptions>, DimensionOptions> currentRegistry, MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        ImmutableMap.Builder<RegistryKey<DimensionOptions>, DimensionOptions> builder = ImmutableMap.builder();

        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : currentRegistry.entrySet()) {
            RegistryKey<DimensionOptions> registryKey = entry.getKey();
            DimensionOptions dimensionOptions = entry.getValue();

            NoiseChunkGenerator currentChunkGenerator = (NoiseChunkGenerator) Objects.requireNonNull(dimensionOptions).chunkGenerator();
            MazeChunkGenerator generator = new MazeChunkGenerator(currentChunkGenerator.getBiomeSource(), currentChunkGenerator.getSettings(), mazeChunkGeneratorConfig);

            RegistryEntry<DimensionType> dimensionTypeEntry = getEntry(dynamicRegistry, currentRegistry, registryKey, dimensionOptions.dimensionTypeEntry().getKey().orElseThrow());
            builder.put(registryKey, new DimensionOptions(dimensionTypeEntry, generator));
        }
        return builder.buildKeepingLast();
    }
}
