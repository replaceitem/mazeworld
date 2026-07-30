package net.replaceitem.mazeworld.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.replaceitem.mazeworld.MazeWorld;
import net.replaceitem.mazeworld.fakes.WorldCreationUIStateAccess;
import net.replaceitem.mazeworld.screen.widget.IntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.LogarithmicIntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.MazePreviewWidget;
import net.replaceitem.mazeworld.types.MazeType;
import net.replaceitem.mazeworld.types.MazeTypes;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CustomizeMazeLevelScreen extends Screen {

    private static final Tooltip infiniteWallTooltip = Tooltip.create(Component.translatable("createWorld.customize.maze_world.infinite_walls.description"));
    
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
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

        var gridWidget = this.layout.addToContents(new GridLayout().spacing(10));
        var helper = gridWidget.createRowHelper(2);

        // Wall block - Edit box
        var wallBlockEditBox = helper.addChild(
                new EditBox(this.font, 0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.empty())
        );
        wallBlockEditBox.setValue(getMazeUiState().getWallBlock().toString());
        wallBlockEditBox.setHint(Component.nullToEmpty("Maze wall block"));
        wallBlockEditBox.setResponder(wallBlockId -> {
            var id = Identifier.tryParse(wallBlockId);
            if(id != null) {
                this.getMazeUiState().setWallBlock(id);
            }
        });

        // Infinite wall - Cycle button
        helper.addChild(
                CycleButton.onOffBuilder(getMazeUiState().isInfiniteWall())
                        .withTooltip(_ -> infiniteWallTooltip)
                        .create(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                                Component.translatable("createWorld.customize.maze_world.infinite_walls"),
                                (_, infiniteWall) -> this.getMazeUiState().setInfiniteWall(infiniteWall)
                        )
        );

        // Maze type - Cycle button
        helper.addChild(
                CycleButton.builder(MazeType::getName, getMazeUiState().getMazeType())
                        .withValues(MazeWorld.MAZE_TYPE_REGISTRY.listElementIds().toList())
                        .withTooltip(mazeType -> Tooltip.create(
                                Component.empty().append(MazeType.getName(mazeType).copy().withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD))
                                        .append("\n").append(MazeType.getDescription(mazeType))
                        ))
                        .create(
                                0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.translatable("createWorld.customize.maze_world.maze_type"),
                                (_, type) -> this.getMazeUiState().setMazeType(type)
                        )
        );

        this.mazeTypeSpecificWidgets = helper.addChild(new GridLayout().spacing(10), 2);

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
            this.mazePreviewWidget.updateConfig(((WorldCreationUIStateAccess) state).getMazeworldState().createMazeConfig());
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
                    )
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
                    )
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
                    )
            );
        }

        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
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
