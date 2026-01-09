package net.replaceitem.mazeworld.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldDimensions;
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
@Mixin(WorldDimensions.class)
public abstract class DimensionOptionsRegistryHolderMixin implements DimensionOptionsRegistryHolderAccess {
    @Shadow @Final private Map<ResourceKey<LevelStem>, LevelStem> dimensions;

    /**
     * Replica of with(), except applied to all dimensions
     */
    @Override
    public WorldDimensions globalWith(RegistryAccess dynamicRegistryManager, MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        Registry<DimensionType> registry = dynamicRegistryManager.lookupOrThrow(Registries.DIMENSION_TYPE);
        Map<ResourceKey<LevelStem>, LevelStem> registry2 = createGlobalRegistry(registry, this.dimensions, mazeChunkGeneratorConfig);
        return new WorldDimensions(registry2);
    }

    @Unique
    private static Holder<DimensionType> getEntry(Registry<DimensionType> dynamicRegistry, Map<ResourceKey<LevelStem>, LevelStem> currentRegistry, ResourceKey<LevelStem> dimensionOptionsRegistryKey, ResourceKey<DimensionType> dimensionTypeRegistryKey) {
        LevelStem dimensionOptions = currentRegistry.get(dimensionOptionsRegistryKey);
        return dimensionOptions == null ? dynamicRegistry.getOrThrow(dimensionTypeRegistryKey) : dimensionOptions.type();
    }

    @Unique
    private static Map<ResourceKey<LevelStem>, LevelStem> createGlobalRegistry(Registry<DimensionType> dynamicRegistry, Map<ResourceKey<LevelStem>, LevelStem> currentRegistry, MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        ImmutableMap.Builder<ResourceKey<LevelStem>, LevelStem> builder = ImmutableMap.builder();

        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : currentRegistry.entrySet()) {
            ResourceKey<LevelStem> registryKey = entry.getKey();
            LevelStem dimensionOptions = entry.getValue();

            NoiseBasedChunkGenerator currentChunkGenerator = (NoiseBasedChunkGenerator) Objects.requireNonNull(dimensionOptions).generator();
            MazeChunkGenerator generator = new MazeChunkGenerator(currentChunkGenerator.getBiomeSource(), currentChunkGenerator.generatorSettings(), mazeChunkGeneratorConfig);

            Holder<DimensionType> dimensionTypeEntry = getEntry(dynamicRegistry, currentRegistry, registryKey, dimensionOptions.type().unwrapKey().orElseThrow());
            builder.put(registryKey, new LevelStem(dimensionTypeEntry, generator));
        }
        return builder.buildKeepingLast();
    }
}
