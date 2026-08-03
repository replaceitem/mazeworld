package net.replaceitem.mazeworld.screen;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.replaceitem.mazeworld.config.MazeGeneratorConfig;
import net.replaceitem.mazeworld.config.MazeType;
import net.replaceitem.mazeworld.config.MazeTypes;
import net.replaceitem.mazeworld.config.StructureReplacementType;
import net.replaceitem.mazeworld.config.types.*;
import net.replaceitem.mazeworld.fakes.WorldDimensionsAccess;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MazeWorldCreationUIState {
    private final WorldCreationUiState worldCreationUiState;
    private final List<Consumer<MazeWorldCreationUIState>> listeners = new ArrayList<>();

    private boolean generateMaze = false;

    private boolean infiniteWall = true;
    private Identifier wallBlock = BlockItemIds.BEDROCK.block().identifier();
    private int minY = Integer.MIN_VALUE;
    private int maxY = Integer.MAX_VALUE;
    private BlockReplacementType replace = BlockReplacementType.ALL;
    private StructureReplacementType replaceStructures = StructureReplacementType.PRESERVE_ESSENTIAL;

    private ResourceKey<MapCodec<? extends MazeType>> mazeType = MazeTypes.WANG_TILES;

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
        return new MazeGeneratorConfig(infiniteWall, wallBlock, minY, maxY, replace.getBlockPredicate(), replaceStructures, mazeType);
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

    public int getMinY() {
        return minY;
    }
    public void setMinY(int minY) {
        this.minY = minY;
        this.onChanged();
    }

    public int getMaxY() {
        return maxY;
    }
    public void setMaxY(int maxY) {
        this.maxY = maxY;
        this.onChanged();
    }

    public BlockReplacementType getReplace() {
        return replace;
    }
    public void setReplace(BlockReplacementType replace) {
        this.replace = replace;
        this.onChanged();
    }

    public StructureReplacementType getReplaceStructures() {
        return replaceStructures;
    }
    public void setReplaceStructures(StructureReplacementType replaceStructures) {
        this.replaceStructures = replaceStructures;
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

    public enum BlockReplacementType {
        ALL("all", BlockPredicate::alwaysTrue),
        REPLACEABLE("replaceable", BlockPredicate::replaceable),
        AIR("air", () -> BlockPredicate.ONLY_IN_AIR_PREDICATE);

        private final Supplier<BlockPredicate> blockPredicate;
        private final Component displayName;

        BlockReplacementType(String name, Supplier<BlockPredicate> blockPredicate) {
            this.blockPredicate = blockPredicate;
            this.displayName = Component.translatable("createWorld.customize.maze_world.replace." + name);
        }

        public BlockPredicate getBlockPredicate() {
            return blockPredicate.get();
        }

        public Component getDisplayName() {
            return displayName;
        }
    }
}
