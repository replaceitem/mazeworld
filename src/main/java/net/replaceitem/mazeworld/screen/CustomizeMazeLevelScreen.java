package net.replaceitem.mazeworld.screen;

import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SwitchGrid;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.replaceitem.mazeworld.MazeWorld;
import net.replaceitem.mazeworld.config.MazeType;
import net.replaceitem.mazeworld.config.MazeTypes;
import net.replaceitem.mazeworld.config.StructureReplacementType;
import net.replaceitem.mazeworld.fakes.WorldCreationUIStateAccess;
import net.replaceitem.mazeworld.screen.widget.IntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.LogarithmicIntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.MappedIntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.MazePreviewWidget;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class CustomizeMazeLevelScreen extends Screen {

    private static final Tooltip INFINITE_WALL_TOOLTIP = Tooltip.create(Component.translatable("createWorld.customize.maze_world.infinite_walls.description"));
    private static final Map<ResourceKey<LevelStem>, Integer> DIMENSION_ORDER = Map.of(
            LevelStem.OVERWORLD, 0,
            LevelStem.NETHER, 1,
            LevelStem.END, 2
    );
    
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    @Nullable
    private ScrollableLayout scrollableLayout;
    protected final CreateWorldScreen parent;

    @UnknownNullability
    private MazePreviewWidget mazePreviewWidget;

    @Nullable
    private GridLayout mazeTypeSpecificWidgets;

    private final List<Runnable> onRemoveCleanup = new ArrayList<>();


    public CustomizeMazeLevelScreen(CreateWorldScreen parent) {
        super(Component.translatable("createWorld.customize.maze_world.title"));
        this.parent = parent;
    }


    private MazeWorldCreationUIState getMazeUiState() {
        return ((WorldCreationUIStateAccess) this.parent.getUiState()).getMazeworldState();
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);

        var spacing = 10;
        var gridWidget = new GridLayout().spacing(spacing);
        this.scrollableLayout = this.layout.addToContents(new ScrollableLayout(this.minecraft, gridWidget, this.layout.getContentHeight()));
        var helper = gridWidget.createRowHelper(2);

        // Heading - General
        helper.addChild(new StringWidget(Component.translatable("createWorld.customize.maze_world.heading.general").withStyle(style -> style.withUnderlined(true)), this.getFont()), 2);

        // Wall block - Edit box
        var wallBlockEditBox = helper.addChild(
                new EditBox(this.font, 0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.empty())
        );
        wallBlockEditBox.setValue(getMazeUiState().getWallBlock().toString());
        wallBlockEditBox.setHint(Component.translatable("createWorld.customize.maze_world.wall_block"));
        wallBlockEditBox.setResponder(wallBlockId -> {
            var id = Identifier.tryParse(wallBlockId);
            if(id != null) {
                this.getMazeUiState().setWallBlock(id);
            }
        });

        // Infinite wall - Cycle button
        helper.addChild(
                CycleButton.onOffBuilder(getMazeUiState().isInfiniteWall())
                        .withTooltip(_ -> INFINITE_WALL_TOOLTIP)
                        .create(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                                Component.translatable("createWorld.customize.maze_world.infinite_walls"),
                                (_, infiniteWall) -> this.getMazeUiState().setInfiniteWall(infiniteWall)
                        )
        );

        var dimensionsHeightRange = this.parent.getUiState().getSettings().selectedDimensions().dimensions().values().stream().flatMapToInt(stem -> {
            var dim = stem.type().value();
            return IntStream.of(dim.minY(), dim.minY() + dim.height());
        }).summaryStatistics();
        var minMinY = dimensionsHeightRange.getMin();
        var maxMaxY = dimensionsHeightRange.getMax();

        // Min Y - Slider
        helper.addChild(
                new MappedIntegerSliderWidget<Integer>(
                        0, 0, Button.DEFAULT_WIDTH,
                        Component.translatable("createWorld.customize.maze_world.min_y"),
                        getMazeUiState().getMinY(), minMinY - 1, maxMaxY,
                        value -> value == Integer.MIN_VALUE ? (minMinY - 1) : value,
                        value -> value == (minMinY - 1) ? Integer.MIN_VALUE : value,
                        (_, value) -> this.getMazeUiState().setMinY(value)
                ) {
                    @Override
                    protected void updateMessage() {
                        var value = getMappedValue();
                        var isNone = value == Integer.MIN_VALUE;
                        this.setMessage(Component.empty().append(name).append(": ")
                                .append(isNone ? Component.translatable("gui.none") : Component.literal(String.valueOf(value))));
                    }
                }
        );

        // Max Y - Slider
        helper.addChild(
                new MappedIntegerSliderWidget<Integer>(
                        0, 0, Button.DEFAULT_WIDTH,
                        Component.translatable("createWorld.customize.maze_world.max_y"),
                        getMazeUiState().getMaxY(), minMinY, maxMaxY + 1,
                        value -> value == Integer.MAX_VALUE ? (maxMaxY + 1) : value,
                        value -> value == (maxMaxY + 1) ? Integer.MAX_VALUE : value,
                        (slider, value) -> this.getMazeUiState().setMaxY(value)
                ) {
                    @Override
                    protected void updateMessage() {
                        var value = getMappedValue();
                        var isNone = value == Integer.MAX_VALUE;
                        this.setMessage(Component.empty().append(name).append(": ")
                                .append(isNone ? Component.translatable("gui.none") : Component.literal(String.valueOf(value))));
                    }
                }
        );

        // Structure replacement type - Cycle button
        helper.addChild(
                CycleButton.builder(MazeWorldCreationUIState.BlockReplacementType::getDisplayName, getMazeUiState().getReplace())
                        .withValues(MazeWorldCreationUIState.BlockReplacementType.values())
                        .create(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                                Component.translatable("createWorld.customize.maze_world.replace"),
                                (_, value) -> this.getMazeUiState().setReplace(value)
                        )
        );

        // Structure replacement type - Cycle button
        helper.addChild(
                CycleButton.builder(StructureReplacementType::getDisplayName, getMazeUiState().getReplaceStructures())
                        .withValues(StructureReplacementType.values())
                        .withTooltip(s -> Tooltip.create(
                                Component.empty().append(
                                        s.getDisplayName().copy().withStyle(style -> style.withUnderlined(true))
                                ).append("\n").append(
                                        s.getDescription()
                                )
                        ))
                        .create(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                                Component.translatable("createWorld.customize.maze_world.structure_replacement"),
                                (_, value) -> this.getMazeUiState().setReplaceStructures(value)
                        )
        );

        // Heading - Dimensions
        helper.addChild(new StringWidget(Component.translatable("createWorld.customize.maze_world.heading.enabled_dimensions").withStyle(style -> style.withUnderlined(true)), this.getFont()), 2);

        // Dinemsions enabled - Switch grid
        SwitchGrid.Builder switchGridBuilder = SwitchGrid.builder(Button.DEFAULT_WIDTH * 2 + spacing);
        var dimensionKeys = this.parent.getUiState().getSettings().selectedDimensions().dimensions().keySet().stream()
                .sorted(Comparator.comparing((ResourceKey<LevelStem> key) -> DIMENSION_ORDER.getOrDefault(key, Integer.MAX_VALUE)).thenComparing(ResourceKey::toString, Comparator.naturalOrder()))
                .toList();
        for (ResourceKey<LevelStem> key : dimensionKeys) {
            switchGridBuilder.addSwitch(
                    Component.literal(key.identifier().toString()),
                    () -> getMazeUiState().getEnabledDimensions().getOrDefault(key, true),
                    enabled -> getMazeUiState().setDimensionEnabled(key, enabled)
            );
        }
        SwitchGrid switchGrid = switchGridBuilder.build();
        helper.addChild(switchGrid.layout(), 2);

        // Heading - Maze generator
        helper.addChild(new StringWidget(Component.translatable("createWorld.customize.maze_world.heading.maze_generator").withStyle(style -> style.withUnderlined(true)), this.getFont()), 2);

        // Maze type - Cycle button
        helper.addChild(
                CycleButton.builder(MazeType::getName, getMazeUiState().getMazeType())
                        .withValues(MazeWorld.MAZE_TYPE_REGISTRY.listElementIds().toList())
                        .withTooltip(mazeType -> Tooltip.create(
                                Component.empty().append(
                                        MazeType.getName(mazeType).copy().withStyle(style -> style.withUnderlined(true))
                                ).append("\n").append(
                                        MazeType.getDescription(mazeType)
                                )
                        ))
                        .create(
                                0, 0, Button.DEFAULT_WIDTH * 2 + spacing, Button.DEFAULT_HEIGHT, Component.translatable("createWorld.customize.maze_world.maze_type"),
                                (_, type) -> this.getMazeUiState().setMazeType(type)
                        ),
                2
        );

        this.mazeTypeSpecificWidgets = helper.addChild(new GridLayout().spacing(spacing), 2);

        mazePreviewWidget = helper.addChild(
                new MazePreviewWidget(
                        this.width / 2 - 10 * 16 / 2, height - 30 - 5 * 16, 160, 80, this.minecraft.getTextureManager()
                ),
                2, LayoutSettings.defaults().alignHorizontallyCenter()
        );

        LinearLayout footerLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footerLayout.defaultCellSetting().alignVerticallyMiddle();
        footerLayout.addChild(
                Button.builder(CommonComponents.GUI_DONE, _ -> onClose()).build()
        );

        this.onRemoveCleanup.clear();

        Consumer<WorldCreationUiState> stateChangeListener = state -> {
            switchGrid.refreshStates();
            this.mazePreviewWidget.updateConfig(((WorldCreationUIStateAccess) state).getMazeworldState().createMazeGeneratorConfig());
            this.mazePreviewWidget.preRender();
        };
        this.parent.getUiState().addListener(stateChangeListener);
        this.onRemoveCleanup.add(() -> ((WorldCreationUIStateAccess) this.parent.getUiState()).removeListener(stateChangeListener));
        stateChangeListener.accept(this.parent.getUiState());

        var unregisterMazeTypeChangeListener = this.getMazeUiState().addMazeTypeChangeListener(_ -> initMazeTypeSpecificWidgets());
        this.onRemoveCleanup.add(unregisterMazeTypeChangeListener);

        this.layout.visitWidgets(this::addRenderableWidget);


        this.initMazeTypeSpecificWidgets();

        this.repositionElements();
    }

    private void initMazeTypeSpecificWidgets() {
        if(this.mazeTypeSpecificWidgets == null) return;
        this.mazeTypeSpecificWidgets.visitWidgets(this::removeWidget);
        this.mazeTypeSpecificWidgets.removeChildren();
        var helper = this.mazeTypeSpecificWidgets.createRowHelper(2);

        // Size - Slider
        if(getMazeUiState().isOfType(MazeTypes.BINARY_TREE, MazeTypes.WANG_TILES, MazeTypes.ROUND_WANG_TILES, MazeTypes.SIMPLEX_NOISE, MazeTypes.SIMPLEX_NOISE_3D)) {
            helper.addChild(
                    new LogarithmicIntegerSliderWidget(0, 0, Button.DEFAULT_WIDTH,
                            Component.translatable("createWorld.customize.maze_world.spacing"),
                            getMazeUiState().getSize(), 2, 1024,
                            (_, size) -> this.getMazeUiState().setSize(size)
                    )
            );
        }

        // Bias - Slider
        if(getMazeUiState().isOfType(MazeTypes.BINARY_TREE)) {
            helper.addChild(
                    new IntegerSliderWidget(
                            0, 0, Button.DEFAULT_WIDTH,
                            Component.translatable("createWorld.customize.maze_world.bias"),
                            (int) (getMazeUiState().getBias() * 100), 0, 100,
                            (slider, _) -> this.getMazeUiState().setBias((float) slider.getPercentageValue())
                    ) {
                        @Override
                        protected void updateMessage() {
                            this.setMessage(Component.empty().append(name).append(": " + getIntegerValue() + "%"));
                        }
                    }
            );
        }

        // Wall width - Slider
        if(getMazeUiState().isOfType(MazeTypes.WANG_TILES, MazeTypes.ROUND_WANG_TILES)) {
            helper.addChild(
                    new IntegerSliderWidget(
                            0, 0, Button.DEFAULT_WIDTH,
                            Component.translatable("createWorld.customize.maze_world.wall_width"),
                            (int) (getMazeUiState().getWallWidth() * 100), 0, 100,
                            (slider, _) -> this.getMazeUiState().setWallWidth((float) slider.getPercentageValue())
                    ) {
                        @Override
                        protected void updateMessage() {
                            this.setMessage(Component.empty().append(name).append(": " + getIntegerValue() + "%"));
                        }
                    }
            );
        }

        // Threshold - Slider
        if(getMazeUiState().isOfType(MazeTypes.SIMPLEX_NOISE, MazeTypes.SIMPLEX_NOISE_3D)) {
            helper.addChild(
                    new IntegerSliderWidget(
                            0, 0, Button.DEFAULT_WIDTH,
                            Component.translatable("createWorld.customize.maze_world.threshold"),
                            (int) (getMazeUiState().getThreshold() * 100), 0, 100,
                            (slider, _) -> this.getMazeUiState().setThreshold(slider.getPercentageValue())
                    ) {
                        @Override
                        protected void updateMessage() {
                            this.setMessage(Component.empty().append(name).append(": " + getIntegerValue() + "%"));
                        }
                    }
            );
        }

        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.scrollableLayout != null) {
            this.scrollableLayout.setMaxHeight(this.layout.getContentHeight());
        }
        this.layout.arrangeElements();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(this.parent);
    }

    @Override
    public void removed() {
        this.onRemoveCleanup.forEach(Runnable::run);
        super.removed();
    }
}
