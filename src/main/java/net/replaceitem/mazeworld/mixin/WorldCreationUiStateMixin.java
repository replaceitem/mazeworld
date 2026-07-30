package net.replaceitem.mazeworld.mixin;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.replaceitem.mazeworld.fakes.WorldCreationUIStateAccess;
import net.replaceitem.mazeworld.screen.MazeWorldCreationUIState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.function.Consumer;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin implements WorldCreationUIStateAccess {
    @Shadow @Final private List<Consumer<WorldCreationUiState>> listeners;

    @Unique private final MazeWorldCreationUIState mazeWorldCreationUIState = new MazeWorldCreationUIState(((WorldCreationUiState)(Object) this));

    @Unique
    @Override
    public MazeWorldCreationUIState getMazeworldState() {
        return mazeWorldCreationUIState;
    }

    @Unique
    @Override
    public void removeListener(Consumer<WorldCreationUiState> listener) {
        this.listeners.remove(listener);
    }
}
