package net.replaceitem.mazeworld;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public record MazeType(
        Function<MazeGeneratorConfig, MazeGenerator<?>> constructor,
        Component name,
        Component description
) {
    public static Codec<MazeType> CODEC = MazeWorld.MAZE_TYPE_REGISTRY.byNameCodec();

    public MazeGenerator<?> getGenerator(MazeGeneratorConfig config) {
        return constructor.apply(config);
    }
}
