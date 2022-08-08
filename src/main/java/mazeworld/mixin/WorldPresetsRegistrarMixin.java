package mazeworld.mixin;

import mazeworld.MazeChunkGenerator;
import mazeworld.MazeChunkGeneratorConfig;
import mazeworld.MazeWorld;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldPresets.Registrar.class)
public abstract class WorldPresetsRegistrarMixin {
    @Shadow @Final private Registry<WorldPreset> worldPresetRegistry;

    @Shadow protected abstract WorldPreset createPreset(DimensionOptions dimensionOptions);

    private static final DimensionOptions OVERWORLD_OPTIONS = new DimensionOptions(
            BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(DimensionTypes.OVERWORLD),
            new MazeChunkGenerator(BuiltinRegistries.STRUCTURE_SET, BuiltinRegistries.NOISE_PARAMETERS, MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(BuiltinRegistries.BIOME), BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.OVERWORLD), MazeChunkGeneratorConfig.getDefaultConfig())
    );
    
    @Inject(method = "initAndGetDefault", at = @At("RETURN"))
    private void addWorldPreset(CallbackInfoReturnable<RegistryEntry<WorldPreset>> cir) {
        BuiltinRegistries.add(this.worldPresetRegistry, MazeWorld.MAZE_WORLD, this.createPreset(OVERWORLD_OPTIONS));
        // USE THIS WHEN ADDING NETHER AND END
//        BuiltinRegistries.add(BuiltinRegistries.WORLD_PRESET, MAZE_WORLD, new WorldPreset(Map.of(DimensionOptions.OVERWORLD, OVERWORLD_OPTIONS)));
    }
}
