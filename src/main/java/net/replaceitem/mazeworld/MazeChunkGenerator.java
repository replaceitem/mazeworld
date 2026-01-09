package net.replaceitem.mazeworld;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class MazeChunkGenerator extends NoiseBasedChunkGenerator {

    public static final MapCodec<MazeChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings),
                    MazeChunkGeneratorConfig.CODEC.fieldOf("maze_settings").forGetter(MazeChunkGenerator::getConfig)
            ).apply(instance, instance.stable(MazeChunkGenerator::new)));

    private final MazeGenerator<?> mazeGenerator;

    public MazeChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> chunkGeneratorSettings, MazeChunkGeneratorConfig mazeConfig) {
        super(biomeSource, chunkGeneratorSettings);
        this.mazeGenerator = mazeConfig.mazeType.getGenerator(mazeConfig);
    }

    public MazeChunkGeneratorConfig getConfig() {
        return mazeGenerator.config;
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureAccessor) {
        super.applyBiomeDecoration(world, chunk, structureAccessor);
        mazeGenerator.generateChunk(world, chunk);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

}
