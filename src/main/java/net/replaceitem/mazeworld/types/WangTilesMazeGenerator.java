package net.replaceitem.mazeworld.types;

import io.netty.util.collection.ByteObjectHashMap;
import io.netty.util.collection.ByteObjectMap;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator2D;
import net.replaceitem.mazeworld.Tile;

import java.util.ArrayList;
import java.util.List;

public abstract class WangTilesMazeGenerator extends MazeGenerator2D {
    public WangTilesMazeGenerator(MazeChunkGeneratorConfig config) {
        super(config);
        this.registerTiles();
    }


    private final ByteObjectMap<Tile> tilesByWalls = new ByteObjectHashMap<>();
    private final List<Tile> determinableTiles = new ArrayList<>();

    public void register(Tile tile) {
        tilesByWalls.put(tile.wallState, tile);
        determinableTiles.add(tile);
    }

    public void registerIndeterminable(Tile tile) {
        tilesByWalls.put(tile.wallState, tile);
    }

    public void register4(Tile tile) {
        for (int i = 0; i < 4; i++) {
            register(tile.rotated(i));
        }
    }


    protected abstract void registerTiles();

    @Override
    public BlockChecker2D getBlockChecker(long worldSeed) {
        int spacing = config.spacing;
        LongObjectMap<Tile> tileCache = new LongObjectHashMap<>();
        return (x, z) -> {
            int tx = Math.floorDiv(x, spacing);
            int tz = Math.floorDiv(z, spacing);
            Tile tile = computeTileAt(tx, tz, worldSeed, tileCache);
            double tilePosX = ((double) Math.floorMod(x, spacing)) / spacing;
            double tilePosY = ((double) Math.floorMod(z, spacing)) / spacing;
            return tile.isBlock(tilePosX, tilePosY);
        };
    }

    private Tile computeTileAt(int tx, int tz, long worldSeed, LongObjectMap<Tile> tileCache) {
        long pos = Tile.tilePosToLong(tx, tz);
        Tile tile = tileCache.get(pos);
        if(tile == null) {
            tile = this.computeTileAt(tx, tz, worldSeed);
            tileCache.put(pos, tile);
        }
        return tile;
    }

    private Tile computeTileAt(int tx, int tz, long worldSeed) {
        if(isDeterminedTile(tx, tz)) {
            return getDeterminedTile(tx, tz, worldSeed);
        }
        byte centerWallState = 0;
        centerWallState |= (getDeterminedTile(tx, tz-1, worldSeed).wallState & 0b0010) << 2; // north wall needs to be south wall of above
        centerWallState |= (getDeterminedTile(tx+1, tz, worldSeed).wallState & 0b0001) << 2; // north wall needs to be south wall of above
        centerWallState |= (getDeterminedTile(tx, tz+1, worldSeed).wallState & 0b1000) >> 2; // north wall needs to be south wall of above
        centerWallState |= (getDeterminedTile(tx-1, tz, worldSeed).wallState & 0b0100) >> 2; // north wall needs to be south wall of above
        return tilesByWalls.get(centerWallState);
    }


    
    private Tile getDeterminedTile(int tx, int tz, long worldSeed) {
        return determinableTiles.get(getRandomIntAt(tx, tz, worldSeed, determinableTiles.size()));
    }
    
    private boolean isDeterminedTile(int tx, int tz) {
        return (tx+tz)%2==0;
    }
}
