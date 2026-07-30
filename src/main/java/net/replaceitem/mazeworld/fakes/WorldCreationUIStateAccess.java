package net.replaceitem.mazeworld.fakes;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.replaceitem.mazeworld.screen.MazeWorldCreationUIState;

import java.util.function.Consumer;

public interface WorldCreationUIStateAccess {
    MazeWorldCreationUIState getMazeworldState();
    void removeListener(Consumer<WorldCreationUiState> listener);
}
