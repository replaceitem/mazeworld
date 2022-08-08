package mazeworld;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class MazeWorld implements ModInitializer {

	public static final RegistryKey<WorldPreset> MAZE_WORLD = RegistryKey.of(Registry.WORLD_PRESET_KEY, new Identifier("mazeworld","maze_world"));
	
	@Override
	public void onInitialize() {
		
	}
}
