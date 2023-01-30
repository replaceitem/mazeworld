package net.replaceitem.mazeworld;

import net.replaceitem.mazeworld.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MazeTypes {

    public static Map<String, MazeType> byId = new HashMap<>();
    public static List<MazeType> types = new ArrayList<>();

    public static MazeType register(MazeType type) {
        byId.put(type.id, type);
        types.add(type);
        return type;
    }

    public static final MazeType BINARY_TREE = register(new MazeType("binary_tree", BinaryTreeMazeGenerator::new));
    public static final MazeType WANG_TILES = register(new MazeType("wang_tiles", RectangularWangTilesMazeGenerator::new));
    public static final MazeType ROUND_WANG_TILES = register(new MazeType("round_wang_tiles", RoundWangTilesMazeGenerator::new));
    public static final MazeType SIMPLEX_NOISE = register(new MazeType("simplex_noise", SimplexNoiseMazeGenerator::new));
}
