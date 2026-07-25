package net.replaceitem.mazeworld.fakes;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.resources.Identifier;
import net.replaceitem.mazeworld.MazeGeneratorConfig;
import net.replaceitem.mazeworld.MazeType;

import java.util.function.Consumer;

public interface WorldCreationUIStateAccess {
    MazeGeneratorConfig createMazeConfig();


    boolean getGenerateMaze();
    void setGenerateMaze(boolean generateMaze);

    int getSpacing();
    void setSpacing(int spacing);

    MazeType getMazeType();
    void setMazeType(MazeType mazeType);

    boolean isInfiniteWall();
    void setInfiniteWall(boolean infiniteWall);

    double getThreshold();
    void setThreshold(double threshold);

    Identifier getWallBlock();
    void setWallBlock(Identifier wallBlock);


    void removeListener(Consumer<WorldCreationUiState> listener);
}
