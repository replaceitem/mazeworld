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
import net.replaceitem.mazeworld.generator.MazeGenerator;

@Deprecated()
public class LegacyMazeChunkGenerator extends NoiseBasedChunkGenerator {

    public static final MapCodec<LegacyMazeChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings),
                    LegacyMazeChunkGeneratorConfig.CODEC.fieldOf("maze_settings").forGetter(LegacyMazeChunkGenerator::getConfig)
            ).apply(instance, instance.stable(LegacyMazeChunkGenerator::new)));

    private final LegacyMazeChunkGeneratorConfig legacyConfig;

    private final MazeGenerator<?> mazeGenerator;

    public LegacyMazeChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> chunkGeneratorSettings, LegacyMazeChunkGeneratorConfig legacyConfig) {
        super(biomeSource, chunkGeneratorSettings);
        this.mazeGenerator = legacyConfig.getMigratedConfig().createGenerator();
        this.legacyConfig = legacyConfig;
    }

    public LegacyMazeChunkGeneratorConfig getConfig() {
        return legacyConfig;
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
