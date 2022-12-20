package net.replaceitem.mazeworld.mixin;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.replaceitem.mazeworld.MazeChunkGenerator;
import net.replaceitem.mazeworld.MazeWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(WorldPresets.Registrar.class)
public abstract class WorldPresetsRegistrarMixin {

    @Shadow @Final private RegistryEntryLookup<ChunkGeneratorSettings> chunkGeneratorSettingsLookup;

    @Shadow @Final private Registerable<WorldPreset> presetRegisterable;

    @Shadow @Final private RegistryEntryLookup<Biome> biomeLookup;

    private DimensionOptions createOptions(ChunkGenerator chunkGenerator, RegistryEntry<DimensionType> dimensionType) {
        return new DimensionOptions(dimensionType, chunkGenerator);
    }

    private DimensionOptions createOptions(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettings, RegistryEntry<DimensionType> dimensionType) {
        return this.createOptions(new MazeChunkGenerator(biomeSource, chunkGeneratorSettings), dimensionType);
    }
    
    @Inject(method = "bootstrap", at = @At("RETURN"))
    private void addWorldPreset(CallbackInfo ci) {
        MultiNoiseBiomeSource multiNoiseBiomeSource = MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(this.biomeLookup);
        RegistryEntry.Reference<ChunkGeneratorSettings> chunkGeneratorSettings = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
        RegistryEntryLookup<DimensionType> dimensionRegistryLookup = presetRegisterable.getRegistryLookup(RegistryKeys.DIMENSION_TYPE);
        WorldPreset worldPreset = new WorldPreset(Map.of(
                DimensionOptions.OVERWORLD, createOptions(multiNoiseBiomeSource, chunkGeneratorSettings, dimensionRegistryLookup.getOrThrow(DimensionTypes.OVERWORLD)),
                DimensionOptions.NETHER, createOptions(multiNoiseBiomeSource, chunkGeneratorSettings, dimensionRegistryLookup.getOrThrow(DimensionTypes.OVERWORLD)),
                DimensionOptions.END, createOptions(multiNoiseBiomeSource, chunkGeneratorSettings, dimensionRegistryLookup.getOrThrow(DimensionTypes.OVERWORLD))
        ));
        this.presetRegisterable.register(MazeWorld.MAZE_WORLD, worldPreset);
    }
}
