package net.replaceitem.mazeworld.mixin;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.replaceitem.mazeworld.MazeChunkGenerator;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeWorld;
import net.replaceitem.mazeworld.screen.CustomizeMazeLevelScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
@Mixin(LevelScreenProvider.class)
public interface LevelScreenProviderMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Map;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;"))
    private static <K, V> Map<K, V> insertLevelScreenProvider(K k1, V v1, K k2, V v2) {
        LevelScreenProvider factory = (parent, generatorOptionsHolder) -> {
            ChunkGenerator chunkGenerator = generatorOptionsHolder.selectedDimensions().getChunkGenerator();
            return new CustomizeMazeLevelScreen(parent,
                    mazeChunkGeneratorConfig -> {
                        MoreOptionsDialogAccessor moreOptionsDialog = (MoreOptionsDialogAccessor) parent.moreOptionsDialog;
                        moreOptionsDialog.callApply(createModifier(mazeChunkGeneratorConfig, generatorOptionsHolder, ChunkGeneratorSettings.OVERWORLD, DimensionOptions.OVERWORLD, DimensionTypes.OVERWORLD));
                        moreOptionsDialog.callApply(createModifier(mazeChunkGeneratorConfig, generatorOptionsHolder, ChunkGeneratorSettings.NETHER, DimensionOptions.NETHER, DimensionTypes.THE_NETHER));
                        moreOptionsDialog.callApply(createModifier(mazeChunkGeneratorConfig, generatorOptionsHolder, ChunkGeneratorSettings.END, DimensionOptions.END, DimensionTypes.THE_END));
                    },
                    chunkGenerator instanceof MazeChunkGenerator mazeChunkGenerator ? mazeChunkGenerator.getConfig() : MazeChunkGeneratorConfig.getDefaultConfig()
            );
        };
        return Map.of(k1, v1, k2, v2, (K) Optional.of(MazeWorld.MAZE_WORLD), (V) factory);
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(MazeChunkGeneratorConfig config, GeneratorOptionsHolder generatorOptionsHolder, RegistryKey<ChunkGeneratorSettings> chunkGeneratorSettingsRegistryKey, RegistryKey<DimensionOptions> dimensionOptionsRegistryKey, RegistryKey<DimensionType> dimensionTypeRegistryKey) {
        return (dynamicRegistryManager, dimensionOptionsRegistryHolder) -> {
            Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry = dynamicRegistryManager.get(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
            BiomeSource biomeSource = dimensionOptionsRegistryHolder.getChunkGenerator().getBiomeSource();
            BiomeSource biomeSource1 = generatorOptionsHolder.selectedDimensions().getChunkGenerator().getBiomeSource();
            biomeSource1.getBiomes().forEach(System.out::println);
            RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettings = chunkGeneratorSettingsRegistry.entryOf(chunkGeneratorSettingsRegistryKey);
            // todo - using biome source differently here, might behave differently
            ChunkGenerator chunkGenerator = new MazeChunkGenerator(biomeSource1, chunkGeneratorSettings,  config);
            return with(dimensionOptionsRegistryHolder, dynamicRegistryManager, chunkGenerator, dimensionOptionsRegistryKey, dimensionTypeRegistryKey);
        };
    }

    private static DimensionOptionsRegistryHolder with(DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder, DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, RegistryKey<DimensionOptions> dimensionOptionsRegistryKey, RegistryKey<DimensionType> dimensionTypeRegistryKey) {
        Registry<DimensionType> registry = dynamicRegistryManager.get(RegistryKeys.DIMENSION_TYPE);
        Registry<DimensionOptions> registry2 = createRegistry(registry, dimensionOptionsRegistryHolder.dimensions(), chunkGenerator, dimensionOptionsRegistryKey, dimensionTypeRegistryKey);
        return new DimensionOptionsRegistryHolder(registry2);
    }

    private static Registry<DimensionOptions> createRegistry(Registry<DimensionType> dynamicRegistry, Registry<DimensionOptions> currentRegistry, ChunkGenerator chunkGenerator, RegistryKey<DimensionOptions> dimensionOptionsRegistryKey, RegistryKey<DimensionType> dimensionTypeRegistryKey) {
        DimensionOptions dimensionOptions = currentRegistry.get(dimensionOptionsRegistryKey);
        RegistryEntry<DimensionType> registryEntry = dimensionOptions == null ? dynamicRegistry.entryOf(dimensionTypeRegistryKey) : dimensionOptions.dimensionTypeEntry();
        return createRegistry(currentRegistry, registryEntry, chunkGenerator, dimensionOptionsRegistryKey);
    }

    private static Registry<DimensionOptions> createRegistry(Registry<DimensionOptions> currentRegistry, RegistryEntry<DimensionType> overworldEntry, ChunkGenerator chunkGenerator, RegistryKey<DimensionOptions> dimensionOptionsRegistryKey) {
        SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(RegistryKeys.DIMENSION, Lifecycle.experimental());
        mutableRegistry.add(dimensionOptionsRegistryKey, new DimensionOptions(overworldEntry, chunkGenerator), Lifecycle.stable());
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : currentRegistry.getEntrySet()) {
            RegistryKey<DimensionOptions> registryKey = entry.getKey();
            if (registryKey == dimensionOptionsRegistryKey) continue;
            mutableRegistry.add(registryKey, entry.getValue(), currentRegistry.getEntryLifecycle(entry.getValue()));
        }
        return mutableRegistry.freeze();
    }
}
