package net.replaceitem.mazeworld.worldgen.maze;

import java.util.Random;

public abstract class MazeGenerator {
    public boolean is3d() {
        return false;
    }
    public abstract BlockChecker getBlockChecker(long seed);

    @FunctionalInterface
    public interface BlockChecker {
        boolean isBlockAt(int x, int y, int z);
    }

    public static Random getMultiSeededRandom(long seed, int... ints) {
        for (int num : ints) {
            seed = new Random(seed+num).nextLong();
        }
        return new Random(seed);
    }

    public static int getRandomIntAt(int x, int y, long seed, int max) {
        return getMultiSeededRandom(seed, x, y).nextInt(max);
    }
}
