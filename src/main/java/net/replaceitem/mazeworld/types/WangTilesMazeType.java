package net.replaceitem.mazeworld.types;

import io.netty.util.collection.ByteObjectHashMap;
import io.netty.util.collection.ByteObjectMap;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeType;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class WangTilesMazeType extends MazeType {
    public WangTilesMazeType() {
        super("wang_tiles");
    }

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

    private static Tile getTileAt(int tx, int tz, long worldSeed, LongObjectMap<Tile> tileCache) {
        Tile tile;
        if(isDeterminedTile(tx, tz)) {
            tile = getDeterminedTile(tx, tz, worldSeed, tileCache);
        } else {
            byte centerWallState = 0;
            centerWallState |= (getDeterminedTile(tx, tz-1, worldSeed, tileCache).wallState & 0b0010) << 2; // north wall needs to be south wall of above
            centerWallState |= (getDeterminedTile(tx+1, tz, worldSeed, tileCache).wallState & 0b0001) << 2; // north wall needs to be south wall of above
            centerWallState |= (getDeterminedTile(tx, tz+1, worldSeed, tileCache).wallState & 0b1000) >> 2; // north wall needs to be south wall of above
            centerWallState |= (getDeterminedTile(tx-1, tz, worldSeed, tileCache).wallState & 0b0100) >> 2; // north wall needs to be south wall of above
            tile = Tile.tilesByWalls.get(centerWallState);
        }
        return tile;
    }
    
    private static Tile getDeterminedTile(int tx, int tz, long worldSeed, LongObjectMap<Tile> tileCache) {
        long longTilePos = Tile.tilePosToLong(tx, tz);
        Tile tile = tileCache.get(longTilePos);
        if(tile == null) {
            tile = Tile.determinableTiles.get(getRandomIntAt(tx, tz, worldSeed, Tile.determinableTiles.size()));
            tileCache.put(longTilePos, tile);
        }
        return tile;
    }
    
    private static boolean isDeterminedTile(int tx, int tz) {
        return (tx+tz)%2==0;
    }
    
    public static class Tile {
        
        public static ByteObjectMap<Tile> tilesByWalls = new ByteObjectHashMap<>();
        public static List<Tile> determinableTiles = new ArrayList<>();

        private static void register(Tile tile) {
            tilesByWalls.put(tile.wallState, tile);
            determinableTiles.add(tile);
        }

        private static void registerUndeterminable(Tile tile) {
            tilesByWalls.put(tile.wallState, tile);
        }

        private static void register4(Tile tile) {
            for (int i = 0; i < 4; i++) {
                register(tile.rotated(i));
            }
        }
        
        static {
            // empty
            registerUndeterminable(new Tile(0b0000));
            // dead ends
            register4(new Tile( 0b1000, new Rectangle(.2,0,.8,.8)));
            // Straight pices
            register4(new Tile( 0b0101, new Rectangle(0,.2,1,.8)));
            // Curve pices
            register4(new Tile( 0b1001, new Rectangle(.2,0,.8,.8), new Rectangle(0,.2,.8,.8)));
            // T pices
            register4(new Tile( 0b1101, new Rectangle(.2,0,.8,.8), new Rectangle(0,.2,1,.8)));
            // Intersection pice
            register(new Tile( 0b1111, new Rectangle(.2,0,.8,1), new Rectangle(0,.2,1,.8)));
        }
        
        
        protected final Rectangle[] rectangles;
        // binary flags for whether there is a wall is on top|right|bottom|left
        protected final byte wallState;
        
        public Tile(int wallState, Rectangle... rectangles) {
            this.wallState = (byte)(wallState & 0b1111);
            this.rectangles = rectangles;
        }
        
        public boolean isBlock(double x, double y) {
            for (Rectangle rectangle : rectangles) {
                if(rectangle.isInside(x, y)) return false;
            }
            return true;
        }
        
        public Tile rotated(int times) {
            times %= 4;
            Rectangle[] rotatedRects = new Rectangle[this.rectangles.length];
            for (int i = 0; i < rotatedRects.length; i++) {
                rotatedRects[i] = rectangles[i].rotated(times);
            }
            byte newWallState = (byte) (wallState >> times | wallState << 4-times);
            return new Tile(newWallState, rotatedRects);
        }
        
        public static long tilePosToLong(int x, int z) {
            return (long)x & 0xFFFFFFFFL | ((long)z & 0xFFFFFFFFL) << 32;
        }

        public static final class Rectangle {
            private final double xa;
            private final double ya;
            private final double xb;
            private final double yb;

            public Rectangle(double xa, double ya, double xb, double yb) {
                this.xa = Math.min(xa, xb);
                this.ya = Math.min(ya, yb);
                this.xb = Math.max(xa, xb);
                this.yb = Math.max(ya, yb);
            }

            boolean isInside(double x, double y) {
                return x >= xa && x <= xb && y >= ya && y <= yb;
            }

            public Rectangle rotated(int times) {
                Rectangle tmp = this;
                for (int i = 0; i < times; i++) {
                    tmp = tmp.rotated();
                }
                return tmp;
            }

            public Rectangle rotated() {
                return new Rectangle(1 - ya, xa, 1 - yb, xb);
            }
        }
    }
}
