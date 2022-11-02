package net.replaceitem.mazeworld.mixin;

import com.mojang.serialization.Lifecycle;
import net.replaceitem.mazeworld.MazeChunkGenerator;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeWorld;
import net.replaceitem.mazeworld.screen.CustomizeMazeLevelScreen;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
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
            ChunkGenerator chunkGenerator = generatorOptionsHolder.generatorOptions().getChunkGenerator();
            return new CustomizeMazeLevelScreen(parent,
                    mazeChunkGeneratorConfig -> {
                        MoreOptionsDialogAccessor moreOptionsDialog = (MoreOptionsDialogAccessor) parent.moreOptionsDialog;
                        moreOptionsDialog.callApply(createOverworldModifier(mazeChunkGeneratorConfig));
                        moreOptionsDialog.callApply(createNetherModifier(mazeChunkGeneratorConfig));
                        moreOptionsDialog.callApply(createEndModifier(mazeChunkGeneratorConfig));
                    },
                    chunkGenerator instanceof MazeChunkGenerator mazeChunkGenerator ? mazeChunkGenerator.getConfig() : MazeChunkGeneratorConfig.getDefaultConfig()
            );
        };
        return Map.of(k1, v1, k2, v2, (K) Optional.of(MazeWorld.MAZE_WORLD), (V) factory);
    }
    
    // TODO - lots of duplicate stuff here, could be better, but lazy


    private static GeneratorOptionsHolder.RegistryAwareModifier createOverworldModifier(MazeChunkGeneratorConfig config) {
        return (dynamicRegistryManager, generatorOptions) -> {
            Registry<StructureSet> structureSetRegistry = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
            Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegistry = dynamicRegistryManager.get(Registry.NOISE_KEY);
            Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry = dynamicRegistryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
            Registry<Biome> biomeRegistry = dynamicRegistryManager.get(Registry.BIOME_KEY);
            RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettings = chunkGeneratorSettingsRegistry.getOrCreateEntry(ChunkGeneratorSettings.OVERWORLD);
            
            ChunkGenerator chunkGenerator = new MazeChunkGenerator(structureSetRegistry, noiseParametersRegistry, MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(biomeRegistry), chunkGeneratorSettings,  config);
            return GeneratorOptions.create(dynamicRegistryManager, generatorOptions, chunkGenerator);
        };
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createNetherModifier(MazeChunkGeneratorConfig config) {
        return (dynamicRegistryManager, generatorOptions) -> {
            Registry<StructureSet> structureSetRegistry = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
            Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegistry = dynamicRegistryManager.get(Registry.NOISE_KEY);
            Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry = dynamicRegistryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
            Registry<Biome> biomeRegistry = dynamicRegistryManager.get(Registry.BIOME_KEY);
            RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettings = chunkGeneratorSettingsRegistry.getOrCreateEntry(ChunkGeneratorSettings.NETHER);
            ChunkGenerator chunkGenerator = new MazeChunkGenerator(structureSetRegistry, noiseParametersRegistry, MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(biomeRegistry), chunkGeneratorSettings,  config);
            return createNether(dynamicRegistryManager, generatorOptions, chunkGenerator);
        };
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createEndModifier(MazeChunkGeneratorConfig config) {
        return (dynamicRegistryManager, generatorOptions) -> {
            Registry<StructureSet> structureSetRegistry = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
            Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegistry = dynamicRegistryManager.get(Registry.NOISE_KEY);
            Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry = dynamicRegistryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
            RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettings = chunkGeneratorSettingsRegistry.getOrCreateEntry(ChunkGeneratorSettings.END);

            ChunkGenerator chunkGenerator = new MazeChunkGenerator(structureSetRegistry, noiseParametersRegistry, new TheEndBiomeSource(BuiltinRegistries.BIOME), chunkGeneratorSettings,  config);
            return createEnd(dynamicRegistryManager, generatorOptions, chunkGenerator);
        };
    }



    private static GeneratorOptions createNether(DynamicRegistryManager dynamicRegistryManager, GeneratorOptions generatorOptions, ChunkGenerator chunkGenerator) {
        Registry<DimensionType> registry = dynamicRegistryManager.get(Registry.DIMENSION_TYPE_KEY);
        Registry<DimensionOptions> registry2 = getRegistryWithReplacedGenerator(registry, generatorOptions.getDimensions(), chunkGenerator, DimensionOptions.NETHER, DimensionTypes.THE_NETHER);
        return new GeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest(), registry2);
    }

    private static GeneratorOptions createEnd(DynamicRegistryManager dynamicRegistryManager, GeneratorOptions generatorOptions, ChunkGenerator chunkGenerator) {
        Registry<DimensionType> registry = dynamicRegistryManager.get(Registry.DIMENSION_TYPE_KEY);
        Registry<DimensionOptions> registry2 = getRegistryWithReplacedGenerator(registry, generatorOptions.getDimensions(), chunkGenerator, DimensionOptions.END, DimensionTypes.THE_END);
        return new GeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest(), registry2);
    }

    private static Registry<DimensionOptions> getRegistryWithReplacedGenerator(Registry<DimensionType> dimensionTypeRegistry, Registry<DimensionOptions> options, ChunkGenerator overworldGenerator, RegistryKey<DimensionOptions> dimensionOption, RegistryKey<DimensionType> dimensionTypeRegistryKey) {
        DimensionOptions dimensionOptions = options.get(dimensionOption);
        RegistryEntry<DimensionType> registryEntry = dimensionOptions == null ? dimensionTypeRegistry.getOrCreateEntry(dimensionTypeRegistryKey) : dimensionOptions.getDimensionTypeEntry();
        return getRegistryWithReplacedDimensionOptions(options, registryEntry, overworldGenerator, dimensionOption);
    }

    private static Registry<DimensionOptions> getRegistryWithReplacedDimensionOptions(Registry<DimensionOptions> options, RegistryEntry<DimensionType> dimensionType, ChunkGenerator generator, RegistryKey<DimensionOptions> dimensionOption) {
        SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
        ((MutableRegistry<DimensionOptions>)mutableRegistry).add(dimensionOption, new DimensionOptions(dimensionType, generator), Lifecycle.stable());
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : options.getEntrySet()) {
            RegistryKey<DimensionOptions> registryKey = entry.getKey();
            if (registryKey == dimensionOption) continue;
            ((MutableRegistry<DimensionOptions>)mutableRegistry).add(registryKey, entry.getValue(), options.getEntryLifecycle(entry.getValue()));
        }
        return mutableRegistry;
    }
}
