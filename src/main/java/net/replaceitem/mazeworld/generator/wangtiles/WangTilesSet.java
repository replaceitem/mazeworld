package net.replaceitem.mazeworld.generator.wangtiles;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WangTilesSet {
    private final Byte2ObjectMap<WangTile> tilesByWalls = new Byte2ObjectOpenHashMap<>();
    private final List<WangTile> determinableWangTiles = new ArrayList<>();

    public void register(WangTile wangTile) {
        tilesByWalls.put(wangTile.wallState, wangTile);
        determinableWangTiles.add(wangTile);
    }

    public void registerIndeterminable(WangTile wangTile) {
        tilesByWalls.put(wangTile.wallState, wangTile);
    }

    public void register4(WangTile wangTile) {
        for (int i = 0; i < 4; i++) {
            register(wangTile.rotated(i));
        }
    }

    public WangTile getRandomDetermined(Random random) {
        return determinableWangTiles.get(random.nextInt(determinableWangTiles.size()));
    }

    public WangTile getByWalls(byte centerWallState) {
        return this.tilesByWalls.get(centerWallState);
    }
}
