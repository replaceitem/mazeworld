package net.replaceitem.mazeworld;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.replaceitem.mazeworld.types.*;

import java.util.function.Function;

public class MazeTypes {
    public static final ResourceKey<MazeType> BINARY_TREE = register("binary_tree", BinaryTreeMazeGenerator::new);
    public static final ResourceKey<MazeType> WANG_TILES = register("wang_tiles", RectangularWangTilesMazeGenerator::new);
    public static final ResourceKey<MazeType> ROUND_WANG_TILES = register("round_wang_tiles", RoundWangTilesMazeGenerator::new);
    public static final ResourceKey<MazeType> SIMPLEX_NOISE = register("simplex_noise", SimplexNoiseMazeGenerator::new);
    public static final ResourceKey<MazeType> SIMPLEX_NOISE_3D = register("simplex_noise_3d", SimplexNoise3DMazeGenerator::new);

    private static ResourceKey<MazeType> register(String name, Function<MazeGeneratorConfig, MazeGenerator<?>> constructor) {
        ResourceKey<MazeType> resourceKey = ResourceKey.create(MazeWorld.MAZE_TYPE_REGISTRY_KEY, MazeWorld.id(name));
        Registry.register(MazeWorld.MAZE_TYPE_REGISTRY, resourceKey, new MazeType(
                constructor,
                Component.translatable("maze_type." + resourceKey.identifier().getPath() + ".name"),
                Component.translatable("maze_type." + resourceKey.identifier().getPath() + ".description")
        ));
        return resourceKey;
    }

    public static ResourceKey<MazeType> bootstrap(Registry<MazeType> registry) {
        return BINARY_TREE;
    }
}
