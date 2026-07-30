package net.replaceitem.mazeworld.types;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.replaceitem.mazeworld.MazeWorld;

public class MazeTypes {
    public static final ResourceKey<MapCodec<? extends MazeType>> BINARY_TREE = register("binary_tree", BinaryTreeMazeConfig.CODEC);
    public static final ResourceKey<MapCodec<? extends MazeType>> WANG_TILES = register("wang_tiles", RectangularWangTilesMazeConfig.CODEC);
    public static final ResourceKey<MapCodec<? extends MazeType>> ROUND_WANG_TILES = register("round_wang_tiles", RoundWangTilesMazeConfig.CODEC);
    public static final ResourceKey<MapCodec<? extends MazeType>> SIMPLEX_NOISE = register("simplex_noise", SimplexNoiseMazeConfig.CODEC);
    public static final ResourceKey<MapCodec<? extends MazeType>> SIMPLEX_NOISE_3D = register("simplex_noise_3d", SimplexNoise3dMazeConfig.CODEC);

    private static ResourceKey<MapCodec<? extends MazeType>> register(String name, MapCodec<? extends MazeType> codec) {
        ResourceKey<MapCodec<? extends MazeType>> resourceKey = ResourceKey.create(MazeWorld.MAZE_TYPE_REGISTRY_KEY, MazeWorld.id(name));
        Registry.register(MazeWorld.MAZE_TYPE_REGISTRY, resourceKey, codec);
        return resourceKey;
    }

    public static ResourceKey<MapCodec<? extends MazeType>> bootstrap() {
        return BINARY_TREE;
    }
}
