package net.replaceitem.mazeworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class MazeChunkGenerator extends NoiseChunkGenerator {

    public static final Codec<MazeChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(NoiseChunkGenerator::getSettings),
                    MazeChunkGeneratorConfig.CODEC.fieldOf("maze_settings").forGetter(MazeChunkGenerator::getConfig)
            ).apply(instance, instance.stable(MazeChunkGenerator::new)));

    private final MazeChunkGeneratorConfig mazeSettings;

    public MazeChunkGeneratorConfig getConfig() {
        return mazeSettings;
    }

    public MazeChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettings) {
        this(biomeSource, chunkGeneratorSettings, MazeChunkGeneratorConfig.getDefaultConfig());
    }

    public MazeChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettings, MazeChunkGeneratorConfig mazeConfig) {
        super(biomeSource, chunkGeneratorSettings);
        this.mazeSettings = mazeConfig;
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

}
