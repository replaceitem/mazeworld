package net.replaceitem.mazeworld;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class MazeWorld implements ModInitializer {

    public static final ResourceKey<Registry<MazeType>> MAZE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(id("maze_type"));
    public static final Registry<MazeType> MAZE_TYPE_REGISTRY = FabricRegistryBuilder.create(MAZE_TYPE_REGISTRY_KEY)
            .attribute(RegistryAttribute.OPTIONAL)
            .buildAndRegister();

	public static final String NAMESPACE = "mazeworld";

    public static final ResourceKey<WorldPreset> LEGACY_MAZE_WORLD_PRESET = ResourceKey.create(Registries.WORLD_PRESET, id("maze_world"));

	@Override
	public void onInitialize() {
		MazeTypes.bootstrap(MAZE_TYPE_REGISTRY);
	}
	
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(NAMESPACE, path);
	}
}
