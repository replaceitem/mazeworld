package net.replaceitem.mazeworld.config.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.replaceitem.mazeworld.config.MazeType;
import net.replaceitem.mazeworld.worldgen.maze.BinaryTreeMazeGenerator;
import net.replaceitem.mazeworld.worldgen.maze.MazeGenerator;

public record BinaryTreeMazeConfig(int spacing, float bias) implements MazeType {
    public static final MapCodec<BinaryTreeMazeConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("spacing").forGetter(BinaryTreeMazeConfig::spacing),
            Codec.FLOAT.fieldOf("bias").forGetter(BinaryTreeMazeConfig::bias)
    ).apply(instance, BinaryTreeMazeConfig::new));

    @Override
    public MapCodec<? extends MazeType> codec() {
        return CODEC;
    }

    @Override
    public MazeGenerator createGenerator() {
        return new BinaryTreeMazeGenerator(spacing, bias);
    }
}
