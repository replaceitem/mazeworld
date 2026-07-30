package net.replaceitem.mazeworld.screen;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.replaceitem.mazeworld.MazeGeneratorConfig;
import net.replaceitem.mazeworld.fakes.WorldDimensionsAccess;
import net.replaceitem.mazeworld.types.*;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class MazeWorldCreationUIState {
    private final WorldCreationUiState worldCreationUiState;
    private final List<Consumer<MazeWorldCreationUIState>> listeners = new ArrayList<>();

    private boolean generateMaze = false;

    private boolean infiniteWall = true;
    private Identifier wallBlock = BlockItemIds.BEDROCK.block().identifier();

    private ResourceKey<MapCodec<? extends MazeType>> mazeType = MazeTypes.BINARY_TREE;

    // specific to the mazeTypes, not every setting is used in all
    private int size = 5;
    private float bias = 0.5f;
    private double threshold = 0.5;
    private float wallWidth = 0.2f;

    public MazeWorldCreationUIState(WorldCreationUiState worldCreationUiState) {
        this.worldCreationUiState = worldCreationUiState;
    }

    public Runnable addMazeTypeChangeListener(Consumer<MazeWorldCreationUIState> listener) {
        this.listeners.add(listener);
        return () -> this.listeners.remove(listener);
    }

    private void onMazeTypeChanged() {
        for (Consumer<MazeWorldCreationUIState> listener : this.listeners) {
            listener.accept(this);
        }
    }

    public @Nullable MazeGeneratorConfig createMazeConfig() {
        var mazeType = createMazeType();
        if(mazeType == null) return null;
        return new MazeGeneratorConfig(infiniteWall, wallBlock, mazeType);
    }

    private @Nullable MazeType createMazeType() {
        if (mazeType.equals(MazeTypes.BINARY_TREE)) {
            return new BinaryTreeMazeConfig(size, bias);
        }
        if (mazeType.equals(MazeTypes.WANG_TILES)) {
            return new RectangularWangTilesMazeConfig(size, wallWidth);
        }
        if (mazeType.equals(MazeTypes.ROUND_WANG_TILES)) {
            return new RoundWangTilesMazeConfig(size, wallWidth);
        }
        if (mazeType.equals(MazeTypes.SIMPLEX_NOISE)) {
            return new SimplexNoiseMazeConfig(size, threshold);
        }
        if (mazeType.equals(MazeTypes.SIMPLEX_NOISE_3D)) {
            return new SimplexNoise3dMazeConfig(size, threshold);
        }
        return null;
    }

    private void onChanged() {
        this.worldCreationUiState.updateDimensions((_, worldDimensions) -> ((WorldDimensionsAccess)(Object) worldDimensions).withMazeGenerator(this.generateMaze ? this.createMazeConfig() : null));
        this.worldCreationUiState.onChanged();
    }


    public boolean getGenerateMaze() {
        return this.generateMaze;
    }
    public void setGenerateMaze(boolean generateMaze) {
        this.generateMaze = generateMaze;
        this.onChanged();
    }

    public boolean isInfiniteWall() {
        return infiniteWall;
    }
    public void setInfiniteWall(boolean infiniteWall) {
        this.infiniteWall = infiniteWall;
        this.onChanged();
    }

    public Identifier getWallBlock() {
        return wallBlock;
    }
    public void setWallBlock(Identifier wallBlock) {
        this.wallBlock = wallBlock;
        this.onChanged();
    }


    public ResourceKey<MapCodec<? extends MazeType>> getMazeType() {
        return mazeType;
    }
    public void setMazeType(ResourceKey<MapCodec<? extends MazeType>> mazeType) {
        this.mazeType = mazeType;
        this.onMazeTypeChanged();
        this.onChanged();
    }
    @SafeVarargs
    public final boolean isOfType(ResourceKey<MapCodec<? extends MazeType>>... types) {
        return Arrays.stream(types).anyMatch(key -> key.equals(this.mazeType));
    }

    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
        this.onChanged();
    }

    public float getBias() {
        return bias;
    }
    public void setBias(float bias) {
        this.bias = bias;
        this.onChanged();
    }

    public double getThreshold() {
        return threshold;
    }
    public void setThreshold(double threshold) {
        this.threshold = threshold;
        this.onChanged();
    }

    public float getWallWidth() {
        return wallWidth;
    }
    public void setWallWidth(float wallWidth) {
        this.wallWidth = wallWidth;
        this.onChanged();
    }
}
