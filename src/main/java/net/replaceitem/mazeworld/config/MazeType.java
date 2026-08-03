package net.replaceitem.mazeworld.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.replaceitem.mazeworld.MazeWorld;
import net.replaceitem.mazeworld.worldgen.maze.MazeGenerator;

import java.util.function.Function;

public interface MazeType {
    Codec<MazeType> CODEC = MazeWorld.MAZE_TYPE_REGISTRY.byNameCodec().dispatchStable(MazeType::codec, Function.identity());

    MapCodec<? extends MazeType> codec();
    MazeGenerator createGenerator();

    static Component getName(ResourceKey<MapCodec<? extends MazeType>> key) {
        return Component.translatable("mazeworld.maze_type." + key.identifier().getPath());
    }
    static Component getDescription(ResourceKey<MapCodec<? extends MazeType>> key) {
        return Component.translatable("mazeworld.maze_type." + key.identifier().getPath() + ".description");
    }
}
