package net.replaceitem.mazeworld.types;

import net.replaceitem.mazeworld.Tile;

public class RoundWangTilesMazeType extends WangTilesMazeType {

    public RoundWangTilesMazeType() {
        super("round_wang_tiles");
    }

    @Override
    protected void registerTiles() {
        // solid
        registerUndeterminable(new Tile.Builder(0b0000).build());


        // dead ends
        register4(new Tile.Builder(0b1000).carve(new Tile.Rectangle(.2,0,.8,.5)).carve(new Tile.Circle(.5, .5, .3)).build());
        // Straight pieces
        register4(new Tile.Builder( 0b0101).carve(new Tile.Rectangle(0,.2,1,.8)).build());
        // Curve pieces
        register4(new Tile.Builder( 0b1001).carve(new Tile.Circle(0, 0, .8)).place(new Tile.Circle(0,0, .2)).build());
        // T pieces
        register4(new Tile.Builder( 0b1101).carve(new Tile.Rectangle(0,0,1,.8)).place(new Tile.Circle(0,0, .2)).place(new Tile.Circle(1,0, .2)).build());

        // Intersection piece
        register(new Tile.Builder( 0b1111).carve(new Tile.Rectangle(0,0,1,1)).place(new Tile.Circle(0,0, .2)).place(new Tile.Circle(1,0, .2)).place(new Tile.Circle(0,1, .2)).place(new Tile.Circle(1,1, .2)).build());
    }
}
