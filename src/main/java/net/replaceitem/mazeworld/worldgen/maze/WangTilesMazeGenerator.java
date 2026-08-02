package net.replaceitem.mazeworld.worldgen.maze;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.replaceitem.mazeworld.worldgen.maze.wangtiles.WangTile;
import net.replaceitem.mazeworld.worldgen.maze.wangtiles.WangTilesSet;

public class WangTilesMazeGenerator extends MazeGenerator {
    private final WangTilesSet tiles;
    private final int tileSize;

    public WangTilesMazeGenerator(WangTilesSet tiles, int tileSize) {
        this.tiles = tiles;
        this.tileSize = tileSize;
    }

    @Override
    public BlockChecker getBlockChecker(long worldSeed) {
        Long2ObjectMap<WangTile> tileCache = new Long2ObjectOpenHashMap<>();
        return (x, _, z) -> {
            int tx = Math.floorDiv(x, tileSize);
            int tz = Math.floorDiv(z, tileSize);
            WangTile wangTile = computeTileAt(tx, tz, worldSeed, tileCache);
            double tilePosX = ((double) Math.floorMod(x, tileSize)) / tileSize;
            double tilePosY = ((double) Math.floorMod(z, tileSize)) / tileSize;
            return wangTile.isBlock(tilePosX, tilePosY);
        };
    }

    private WangTile computeTileAt(int tx, int tz, long worldSeed, Long2ObjectMap<WangTile> tileCache) {
        long pos = WangTile.tilePosToLong(tx, tz);
        return tileCache.computeIfAbsent(pos, _ -> this.computeTileAt(tx, tz, worldSeed));
    }

    private WangTile computeTileAt(int tx, int tz, long worldSeed) {
        if(isDeterminedTile(tx, tz)) {
            return getDeterminedTile(tx, tz, worldSeed);
        }
        byte centerWallState = (byte)(
                  ((getDeterminedTile(tx, tz-1, worldSeed).wallState & 0b0010) << 2) // north wall here is south wall of north tile
                | ((getDeterminedTile(tx+1, tz, worldSeed).wallState & 0b0001) << 2) // east wall here is west wall of east tile
                | ((getDeterminedTile(tx, tz+1, worldSeed).wallState & 0b1000) >> 2) // south wall here is north wall of south tile
                | ((getDeterminedTile(tx-1, tz, worldSeed).wallState & 0b0100) >> 2) // west wall here is east wall of west tile
        );
        return this.tiles.getByWalls(centerWallState);
    }


    
    private WangTile getDeterminedTile(int tx, int tz, long worldSeed) {
        return this.tiles.getRandomDetermined(getMultiSeededRandom(worldSeed, tx, tz));
    }
    
    private boolean isDeterminedTile(int tx, int tz) {
        return (tx+tz)%2==0;
    }
}
