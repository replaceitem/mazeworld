package net.replaceitem.mazeworld.mixin;

import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.replaceitem.mazeworld.MazeGeneratorConfig;
import net.replaceitem.mazeworld.MazeType;
import net.replaceitem.mazeworld.MazeTypes;
import net.replaceitem.mazeworld.MazeWorld;
import net.replaceitem.mazeworld.fakes.WorldCreationUIStateAccess;
import net.replaceitem.mazeworld.fakes.WorldDimensionsAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.function.Consumer;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin implements WorldCreationUIStateAccess {
    @Shadow public abstract void onChanged();

    @Shadow @Final private List<Consumer<WorldCreationUiState>> listeners;
    @Shadow private WorldCreationContext settings;

    @Shadow public abstract void updateDimensions(WorldCreationContext.DimensionsUpdater modifier);

    @Unique private boolean generateMaze = false;
    @Unique private int spacing = 5;
    @Unique private MazeType mazeType = MazeWorld.MAZE_TYPE_REGISTRY.getValueOrThrow(MazeTypes.BINARY_TREE);
    @Unique private boolean infiniteWall = true;
    @Unique private double threshold = 0.5;
    @Unique private Identifier wallBlock = BlockItemIds.BEDROCK.block().identifier();

    @Override
    public MazeGeneratorConfig createMazeConfig() {
        return new MazeGeneratorConfig(
                this.spacing,
                this.mazeType,
                this.infiniteWall,
                this.threshold,
                this.wallBlock
        );
    }

    @Unique
    private void onMazeSettingsChanged() {
        this.updateDimensions((_, worldDimensions) -> ((WorldDimensionsAccess)(Object) worldDimensions).withMazeGenerator(this.generateMaze ? this.createMazeConfig() : null));
        this.onChanged();
    }

    @Override
    public boolean getGenerateMaze() {
        return this.generateMaze;
    }

    @Override
    public void setGenerateMaze(boolean generateMaze) {
        this.generateMaze = generateMaze;
        this.onMazeSettingsChanged();
    }

    @Override
    public int getSpacing() {
        return spacing;
    }

    @Override
    public void setSpacing(int spacing) {
        this.spacing = spacing;
        this.onMazeSettingsChanged();
    }

    @Override
    public MazeType getMazeType() {
        return mazeType;
    }

    @Override
    public void setMazeType(MazeType mazeType) {
        this.mazeType = mazeType;
        this.onMazeSettingsChanged();
    }

    @Override
    public boolean isInfiniteWall() {
        return infiniteWall;
    }

    @Override
    public void setInfiniteWall(boolean infiniteWall) {
        this.infiniteWall = infiniteWall;
        this.onMazeSettingsChanged();
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public void setThreshold(double threshold) {
        this.threshold = threshold;
        this.onMazeSettingsChanged();
    }

    @Override
    public Identifier getWallBlock() {
        return wallBlock;
    }

    @Override
    public void setWallBlock(Identifier wallBlock) {
        this.wallBlock = wallBlock;
        this.onMazeSettingsChanged();
    }


    @Override
    public void removeListener(Consumer<WorldCreationUiState> listener) {
        this.listeners.remove(listener);
    }
}
