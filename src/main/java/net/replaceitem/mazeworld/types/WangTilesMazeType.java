package net.replaceitem.mazeworld.types;

import io.netty.util.collection.ByteObjectHashMap;
import io.netty.util.collection.ByteObjectMap;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeType;
import net.minecraft.util.math.ChunkPos;
import net.replaceitem.mazeworld.Tile;

import java.util.ArrayList;
import java.util.List;

public abstract class WangTilesMazeType extends MazeType {
    public WangTilesMazeType(String id) {
        super(id);
        this.registerTiles();
    }


    private final ByteObjectMap<Tile> tilesByWalls = new ByteObjectHashMap<>();
    private final List<Tile> determinableTiles = new ArrayList<>();

    public void register(Tile tile) {
        tilesByWalls.put(tile.wallState, tile);
        determinableTiles.add(tile);
    }

    public void registerUndeterminable(Tile tile) {
        tilesByWalls.put(tile.wallState, tile);
    }

    public void register4(Tile tile) {
        for (int i = 0; i < 4; i++) {
            register(tile.rotated(i));
        }
    }


    protected abstract void registerTiles();

    @Override
    public BlockChecker getBlockChecker(ChunkPos chunkPos, MazeChunkGeneratorConfig config, long worldSeed) {
        int spacing = config.spacing;
        LongObjectMap<Tile> tileCache = new LongObjectHashMap<>();
        return (x, y) -> {
            int tx = Math.floorDiv(x, spacing);
            int tz = Math.floorDiv(y, spacing);
            Tile tile = getTileAt(tx, tz, worldSeed, tileCache);
            double tilePosX = ((double) Math.floorMod(x, spacing)) / spacing;
            double tilePosY = ((double) Math.floorMod(y, spacing)) / spacing;
            return tile.isBlock(tilePosX, tilePosY);
        };
    }

    private Tile getTileAt(int tx, int tz, long worldSeed, LongObjectMap<Tile> tileCache) {
        Tile tile;
        if(isDeterminedTile(tx, tz)) {
            tile = getDeterminedTile(tx, tz, worldSeed, tileCache);
        } else {
            byte centerWallState = 0;
            centerWallState |= (getDeterminedTile(tx, tz-1, worldSeed, tileCache).wallState & 0b0010) << 2; // north wall needs to be south wall of above
            centerWallState |= (getDeterminedTile(tx+1, tz, worldSeed, tileCache).wallState & 0b0001) << 2; // north wall needs to be south wall of above
            centerWallState |= (getDeterminedTile(tx, tz+1, worldSeed, tileCache).wallState & 0b1000) >> 2; // north wall needs to be south wall of above
            centerWallState |= (getDeterminedTile(tx-1, tz, worldSeed, tileCache).wallState & 0b0100) >> 2; // north wall needs to be south wall of above
            tile = tilesByWalls.get(centerWallState);
        }
        return tile;
    }


    
    private Tile getDeterminedTile(int tx, int tz, long worldSeed, LongObjectMap<Tile> tileCache) {
        long longTilePos = Tile.tilePosToLong(tx, tz);
        Tile tile = tileCache.get(longTilePos);
        if(tile == null) {
            tile = determinableTiles.get(getRandomIntAt(tx, tz, worldSeed, determinableTiles.size()));
            tileCache.put(longTilePos, tile);
        }
        return tile;
    }
    
    private boolean isDeterminedTile(int tx, int tz) {
        return (tx+tz)%2==0;
    }
}
