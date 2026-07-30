package net.replaceitem.mazeworld;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.replaceitem.mazeworld.types.MazeType;
import net.replaceitem.mazeworld.types.MazeTypes;

import java.util.Objects;

public class MazeWorld implements ModInitializer {

    public static final ResourceKey<Registry<MapCodec<? extends MazeType>>> MAZE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(id("maze_type"));
    public static final Registry<MapCodec<? extends MazeType>> MAZE_TYPE_REGISTRY = FabricRegistryBuilder.create(MAZE_TYPE_REGISTRY_KEY)
            .attribute(RegistryAttribute.OPTIONAL)
            .buildAndRegister();

	public static final String NAMESPACE = "mazeworld";

	@Override
	public void onInitialize() {
        Objects.requireNonNull(MazeTypes.bootstrap());
	}
	
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(NAMESPACE, path);
	}
}
