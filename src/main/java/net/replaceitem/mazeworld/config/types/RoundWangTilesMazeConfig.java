package net.replaceitem.mazeworld.config.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.replaceitem.mazeworld.config.MazeType;
import net.replaceitem.mazeworld.worldgen.maze.MazeGenerator;
import net.replaceitem.mazeworld.worldgen.maze.WangTilesMazeGenerator;
import net.replaceitem.mazeworld.worldgen.maze.wangtiles.WangTile;
import net.replaceitem.mazeworld.worldgen.maze.wangtiles.WangTilesSet;

public record RoundWangTilesMazeConfig(int size, float wallWidth) implements MazeType {
    public static final MapCodec<RoundWangTilesMazeConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("size").forGetter(RoundWangTilesMazeConfig::size),
            Codec.FLOAT.fieldOf("wall_width").forGetter(RoundWangTilesMazeConfig::wallWidth)
    ).apply(instance, RoundWangTilesMazeConfig::new));

    @Override
    public MapCodec<? extends MazeType> codec() {
        return CODEC;
    }

    @Override
    public MazeGenerator createGenerator() {
        var tileSet = new WangTilesSet();
        double t = wallWidth * 0.5;

        // solid
        tileSet.registerIndeterminable(new WangTile.Builder(0b0000).build());


        // dead ends
        tileSet.register4(new WangTile.Builder(0b1000).carve(new WangTile.Rectangle(t,0,1-t,.5)).carve(new WangTile.Circle(.5, .5, .5-t)).build());
        // Straight pieces
        tileSet.register4(new WangTile.Builder( 0b0101).carve(new WangTile.Rectangle(0,t,1,1-t)).build());
        // Curve pieces
        tileSet.register4(new WangTile.Builder( 0b1001).carve(new WangTile.Circle(0, 0, 1-t)).place(new WangTile.Circle(0,0, t)).build());
        // T pieces
        tileSet.register4(new WangTile.Builder( 0b1101).carve(new WangTile.Rectangle(0,0,1,1-t)).place(new WangTile.Circle(0,0, t)).place(new WangTile.Circle(1,0, t)).build());

        // Intersection piece
        tileSet.register(new WangTile.Builder( 0b1111).carve(new WangTile.Rectangle(0,0,1,1)).place(new WangTile.Circle(0,0, t)).place(new WangTile.Circle(1,0, t)).place(new WangTile.Circle(0,1, t)).place(new WangTile.Circle(1,1, t)).build());

        return new WangTilesMazeGenerator(tileSet, size);
    }
}
