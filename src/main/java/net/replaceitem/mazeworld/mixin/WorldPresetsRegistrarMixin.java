package net.replaceitem.mazeworld.mixin;

import net.replaceitem.mazeworld.MazeChunkGenerator;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeWorld;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(WorldPresets.Registrar.class)
public abstract class WorldPresetsRegistrarMixin {

    private static final DimensionOptions OVERWORLD_OPTIONS = new DimensionOptions(
            BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(DimensionTypes.OVERWORLD),
            MazeChunkGenerator.forOverworld(MazeChunkGeneratorConfig.getDefaultConfig())
    );

    private static final DimensionOptions NETHER_OPTIONS = new DimensionOptions(
            BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(DimensionTypes.THE_NETHER),
            MazeChunkGenerator.forNether(MazeChunkGeneratorConfig.getDefaultConfig())
    );

    private static final DimensionOptions END_OPTIONS = new DimensionOptions(
            BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(DimensionTypes.THE_END),
            MazeChunkGenerator.forEnd(MazeChunkGeneratorConfig.getDefaultConfig())
    );
    
    @Inject(method = "initAndGetDefault", at = @At("RETURN"))
    private void addWorldPreset(CallbackInfoReturnable<RegistryEntry<WorldPreset>> cir) {
        BuiltinRegistries.add(BuiltinRegistries.WORLD_PRESET, MazeWorld.MAZE_WORLD, new WorldPreset(Map.of(
                DimensionOptions.OVERWORLD, OVERWORLD_OPTIONS,
                DimensionOptions.NETHER, NETHER_OPTIONS,
                DimensionOptions.END, END_OPTIONS
        )));
    }
}
