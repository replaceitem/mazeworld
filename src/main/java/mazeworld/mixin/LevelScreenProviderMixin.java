package mazeworld.mixin;

import mazeworld.MazeChunkGenerator;
import mazeworld.MazeChunkGeneratorConfig;
import mazeworld.MazeWorld;
import mazeworld.screen.CustomizeMazeLevelScreen;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
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
                    mazeChunkGeneratorConfig -> ((MoreOptionsDialogAccessor) parent.moreOptionsDialog).callApply(createModifier(mazeChunkGeneratorConfig)),
                    chunkGenerator instanceof MazeChunkGenerator mazeChunkGenerator ? mazeChunkGenerator.getConfig() : MazeChunkGeneratorConfig.getDefaultConfig()
            );
        };
        return Map.of(k1, v1, k2, v2, (K) Optional.of(MazeWorld.MAZE_WORLD), (V) factory);
    }


    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(MazeChunkGeneratorConfig config) {
        return (dynamicRegistryManager, generatorOptions) -> {
            Registry<StructureSet> structureSetRegistry = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
            Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegistry = dynamicRegistryManager.get(Registry.NOISE_KEY);
            Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry = dynamicRegistryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
            Registry<Biome> biomeRegistry = dynamicRegistryManager.get(Registry.BIOME_KEY);
            RegistryEntry<ChunkGeneratorSettings> overworldChunkGeneratorSettings = chunkGeneratorSettingsRegistry.getOrCreateEntry(ChunkGeneratorSettings.OVERWORLD);
            
            ChunkGenerator chunkGenerator = new MazeChunkGenerator(structureSetRegistry, noiseParametersRegistry, MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(biomeRegistry), overworldChunkGeneratorSettings,  config);
            return GeneratorOptions.create(dynamicRegistryManager, generatorOptions, chunkGenerator);
        };
    }
}
