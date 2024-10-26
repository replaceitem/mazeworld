package net.replaceitem.mazeworld;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;

public class MazeWorld implements ModInitializer {
	
	public static final String NAMESPACE = "mazeworld";

	public static final RegistryKey<WorldPreset> MAZE_WORLD = RegistryKey.of(RegistryKeys.WORLD_PRESET, id("maze_world"));
	
	@Override
	public void onInitialize() {
		
	}
	
	public static Identifier id(String path) {
		return Identifier.of(NAMESPACE, path);
	}
}
