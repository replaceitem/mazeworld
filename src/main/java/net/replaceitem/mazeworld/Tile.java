package net.replaceitem.mazeworld;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    protected final TileOperation[] operations;
    // binary flags for whether there is a wall is on top|right|bottom|left
    public final byte wallState;

    public Tile(int wallState, TileOperation[] operations) {
        this.wallState = (byte)(wallState & 0b1111);
        this.operations = operations;
    }

    public boolean isBlock(double x, double y) {
        for (int i = operations.length - 1; i >= 0; i--) {
            TileOperation operation = operations[i];
            if(operation.shape().isInside(x, y)) {
                return operation.place;
            }
        }
        return true;
    }

    public Tile rotated(int times) {
        times %= 4;
        TileOperation[] rotatedOperations = new TileOperation[this.operations.length];
        for (int i = 0; i < rotatedOperations.length; i++) {
            rotatedOperations[i] = operations[i].rotated(times);
        }
        byte newWallState = (byte) (wallState >> times | wallState << 4-times);
        return new Tile(newWallState, rotatedOperations);
    }

    public static long tilePosToLong(int x, int z) {
        return (long)x & 0xFFFFFFFFL | ((long)z & 0xFFFFFFFFL) << 32;
    }

    private record TileOperation(boolean place, TileShape shape) {
        TileOperation rotated(int times) {
            return new TileOperation(place, shape.rotated(times));
        }
    }

    public static class Builder {
        private final byte wallState;
        private final List<TileOperation> operations = new ArrayList<>();

        public Builder(int wallState) {
            this.wallState = (byte) wallState;
        }

        public Builder place(TileShape shape) {
            operations.add(new TileOperation(true, shape));
            return this;
        }

        public Builder carve(TileShape shape) {
            operations.add(new TileOperation(false, shape));
            return this;
        }

        public Tile build() {
            TileOperation[] operationsArr = new TileOperation[operations.size()];
            return new Tile(wallState, operations.toArray(operationsArr));
        }
    }

    public abstract static class TileShape {

        public TileShape rotated(int times) {
            TileShape tmp = this;
            for (int i = 0; i < times; i++) {
                tmp = tmp.rotated();
            }
            return tmp;
        }

        public abstract TileShape rotated();
        public abstract boolean isInside(double x, double y);
    }


    public static class Rectangle extends Tile.TileShape {
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

        @Override
        public boolean isInside(double x, double y) {
            return x >= xa && x <= xb && y >= ya && y <= yb;
        }

        @Override
        public Rectangle rotated() {
            return new Rectangle(1 - ya, xa, 1 - yb, xb);
        }
    }


    public static class Circle extends Tile.TileShape {
        private final double x;
        private final double y;
        private final double r;

        public Circle(double x, double y, double r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }

        @Override
        public boolean isInside(double px, double py) {
            double dx = px-x;
            double dy = py-y;
            return dx*dx + dy*dy <= r*r;
        }

        @Override
        public Circle rotated() {
            return new Circle(1 - y, x, r);
        }
    }
}
