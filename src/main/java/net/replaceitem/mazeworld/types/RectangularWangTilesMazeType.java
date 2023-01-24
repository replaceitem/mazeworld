package net.replaceitem.mazeworld.types;

import net.replaceitem.mazeworld.Tile;

public class RectangularWangTilesMazeType extends WangTilesMazeType {

    public RectangularWangTilesMazeType() {
        super("wang_tiles");
    }

    @Override
    protected void registerTiles() {
        // solid
        registerUndeterminable(new Tile.Builder(0b0000).build());


        // dead ends
        register4(new Tile.Builder(0b1000).carve(new Tile.Rectangle(.2,0,.8,.8)).build());
        // Straight pieces
        register4(new Tile.Builder( 0b0101).carve(new Tile.Rectangle(0,.2,1,.8)).build());
        // Curve pieces
        register4(new Tile.Builder( 0b1001).carve(new Tile.Rectangle(.2,0,.8,.8)).carve(new Tile.Rectangle(0,.2,.8,.8)).build());
        // T pieces
        register4(new Tile.Builder( 0b1101).carve(new Tile.Rectangle(.2,0,.8,.8)).carve(new Tile.Rectangle(0,.2,1,.8)).build());

        // Intersection piece
        register(new Tile.Builder( 0b1111).carve(new Tile.Rectangle(.2,0,.8,1)).carve(new Tile.Rectangle(0,.2,1,.8)).build());
    }


}
