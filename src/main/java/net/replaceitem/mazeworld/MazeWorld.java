package net.replaceitem.mazeworld;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class MazeWorld implements ModInitializer {
	
	public static final String NAMESPACE = "mazeworld";

	public static final ResourceKey<WorldPreset> MAZE_WORLD = ResourceKey.create(Registries.WORLD_PRESET, id("maze_world"));
	
	@Override
	public void onInitialize() {
		
	}
	
	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
	}
}
