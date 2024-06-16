package net.replaceitem.mazeworld;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;

public class MazeWorld implements ModInitializer {

	public static final RegistryKey<WorldPreset> MAZE_WORLD = RegistryKey.of(RegistryKeys.WORLD_PRESET, Identifier.of("mazeworld","maze_world"));
	
	@Override
	public void onInitialize() {
		
	}
}
