package net.replaceitem.mazeworld;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.WorldPreset;

public class MazeWorld implements ModInitializer {

	public static final RegistryKey<WorldPreset> MAZE_WORLD = RegistryKey.of(Registry.WORLD_PRESET_KEY, new Identifier("net/replaceitem/mazeworld","maze_world"));
	
	@Override
	public void onInitialize() {
		
	}
}
