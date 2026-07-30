package net.replaceitem.mazeworld.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.replaceitem.mazeworld.MazeGeneratorConfig;
import net.replaceitem.mazeworld.generator.MazeGenerator;
import net.replaceitem.mazeworld.generator.WangTilesMazeGenerator;
import net.replaceitem.mazeworld.generator.wangtiles.WangTile;
import net.replaceitem.mazeworld.generator.wangtiles.WangTilesSet;

public record RectangularWangTilesMazeConfig(int size, float wallWidth) implements MazeType {
    public static final MapCodec<RectangularWangTilesMazeConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("size").forGetter(RectangularWangTilesMazeConfig::size),
            Codec.FLOAT.fieldOf("wall_width").forGetter(RectangularWangTilesMazeConfig::wallWidth)
    ).apply(instance, RectangularWangTilesMazeConfig::new));

    @Override
    public MapCodec<? extends MazeType> codec() {
        return CODEC;
    }

    @Override
    public MazeGenerator<?> createGenerator(MazeGeneratorConfig mazeGeneratorConfig) {
        double t = wallWidth * 0.5;

        var tileSet = new WangTilesSet();

        // solid
        tileSet.registerIndeterminable(new WangTile.Builder(0b0000).build());


        // dead ends
        tileSet.register4(new WangTile.Builder(0b1000).carve(new WangTile.Rectangle(t,0,1-t,1-t)).build());
        // Straight pieces
        tileSet.register4(new WangTile.Builder( 0b0101).carve(new WangTile.Rectangle(0,t,1,1-t)).build());
        // Curve pieces
        tileSet.register4(new WangTile.Builder( 0b1001).carve(new WangTile.Rectangle(t,0,1-t,1-t)).carve(new WangTile.Rectangle(0,t,1-t,1-t)).build());
        // T pieces
        tileSet.register4(new WangTile.Builder( 0b1101).carve(new WangTile.Rectangle(t,0,1-t,1-t)).carve(new WangTile.Rectangle(0,t,1,1-t)).build());

        // Intersection piece
        tileSet.register(new WangTile.Builder( 0b1111).carve(new WangTile.Rectangle(t,0,1-t,1)).carve(new WangTile.Rectangle(0,t,1,1-t)).build());

        return new WangTilesMazeGenerator(mazeGeneratorConfig, tileSet, size);
    }
}
