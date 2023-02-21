package net.replaceitem.mazeworld.types;

import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.Tile;

public class RectangularWangTilesMazeGenerator extends WangTilesMazeGenerator {

    public RectangularWangTilesMazeGenerator(MazeChunkGeneratorConfig config) {
        super(config);
    }

    @Override
    protected void registerTiles() {
        double t = this.config.threshold * 0.5;

        // solid
        registerIndeterminable(new Tile.Builder(0b0000).build());


        // dead ends
        register4(new Tile.Builder(0b1000).carve(new Tile.Rectangle(t,0,1-t,1-t)).build());
        // Straight pieces
        register4(new Tile.Builder( 0b0101).carve(new Tile.Rectangle(0,t,1,1-t)).build());
        // Curve pieces
        register4(new Tile.Builder( 0b1001).carve(new Tile.Rectangle(t,0,1-t,1-t)).carve(new Tile.Rectangle(0,t,1-t,1-t)).build());
        // T pieces
        register4(new Tile.Builder( 0b1101).carve(new Tile.Rectangle(t,0,1-t,1-t)).carve(new Tile.Rectangle(0,t,1,1-t)).build());

        // Intersection piece
        register(new Tile.Builder( 0b1111).carve(new Tile.Rectangle(t,0,1-t,1)).carve(new Tile.Rectangle(0,t,1,1-t)).build());
    }


}
