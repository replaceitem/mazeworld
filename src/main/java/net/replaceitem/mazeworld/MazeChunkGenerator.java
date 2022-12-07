package net.replaceitem.mazeworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class MazeChunkGenerator extends NoiseChunkGenerator {

    private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry;
    
    public static final Codec<MazeChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> 
            NoiseChunkGenerator.createStructureSetRegistryGetter(instance).and(instance.group(
                    RegistryOps.createRegistryCodec(Registry.NOISE_KEY).forGetter(generator -> generator.noiseRegistry),
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(generator -> generator.settings),
                    MazeChunkGeneratorConfig.CODEC.fieldOf("maze_settings").forGetter(MazeChunkGenerator::getConfig)
            )).apply(instance, instance.stable(MazeChunkGenerator::new))
    );

    private final MazeChunkGeneratorConfig mazeSettings;
    
    public MazeChunkGeneratorConfig getConfig() {
        return mazeSettings;
    }

    public MazeChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry, BiomeSource populationSource, RegistryEntry<ChunkGeneratorSettings> settings, MazeChunkGeneratorConfig mazeSettings) {
        super(structureSetRegistry, noiseRegistry, populationSource, settings);
        this.mazeSettings = mazeSettings;
        this.noiseRegistry = noiseRegistry;
    }
    
    public static MazeChunkGenerator forOverworld(MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        return new MazeChunkGenerator(BuiltinRegistries.STRUCTURE_SET, BuiltinRegistries.NOISE_PARAMETERS, MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(BuiltinRegistries.BIOME), BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.OVERWORLD), mazeChunkGeneratorConfig);
    }
    public static MazeChunkGenerator forNether(MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        return new MazeChunkGenerator(BuiltinRegistries.STRUCTURE_SET, BuiltinRegistries.NOISE_PARAMETERS, MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(BuiltinRegistries.BIOME), BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.NETHER), mazeChunkGeneratorConfig);
    }
    public static MazeChunkGenerator forEnd(MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        return new MazeChunkGenerator(BuiltinRegistries.STRUCTURE_SET, BuiltinRegistries.NOISE_PARAMETERS, new TheEndBiomeSource(BuiltinRegistries.BIOME), BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.END), mazeChunkGeneratorConfig);
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        super.generateFeatures(world, chunk, structureAccessor);
        
        mazeSettings.mazeType.generateChunk(mazeSettings, world, chunk);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    static {
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier("mazeworld", "maze_world"), CODEC);
    }
}
