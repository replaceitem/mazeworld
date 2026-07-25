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
import net.replaceitem.mazeworld.MazeType;
import net.replaceitem.mazeworld.MazeWorld;
import net.replaceitem.mazeworld.fakes.WorldCreationUIStateAccess;
import net.replaceitem.mazeworld.screen.widget.IntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.LogarithmicIntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.MazePreviewWidget;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class CustomizeMazeLevelScreen extends Screen {

    private static final Tooltip infiniteWallTooltip = Tooltip.create(Component.translatable("createWorld.customize.maze_world.infinite_walls.description"));
    
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    protected final CreateWorldScreen parent;

    @UnknownNullability
    private MazePreviewWidget mazePreviewWidget;

    @Nullable
    private Consumer<WorldCreationUiState> stateChangeListener;


    public CustomizeMazeLevelScreen(CreateWorldScreen parent) {
        super(Component.translatable("createWorld.customize.maze_world.title"));
        this.parent = parent;
    }


    private WorldCreationUIStateAccess getMazeUiState() {
        return ((WorldCreationUIStateAccess) this.parent.getUiState());
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);

        var gridWidget = this.layout.addToContents(new GridLayout());
        gridWidget.spacing(10);
        var helper = gridWidget.createRowHelper(2);

        helper.addChild(
                CycleButton.builder(MazeType::name, getMazeUiState().getMazeType())
                        .withValues(MazeWorld.MAZE_TYPE_REGISTRY.stream().toList())
                        .withTooltip(mazeType1 -> Tooltip.create(
                                mazeType1.name().copy().withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD)
                                        .append("\n").append(mazeType1.description())
                        ))
                        .create(
                                0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.translatable("createWorld.customize.maze_world.maze_type"),
                                (_, type) -> this.getMazeUiState().setMazeType(type)
                        )
        );

        helper.addChild(
                new LogarithmicIntegerSliderWidget(0, 0, Button.DEFAULT_WIDTH,
                        Component.translatable("createWorld.customize.maze_world.spacing"),
                        getMazeUiState().getSpacing(), 2, 1024,
                        (_, spacing) -> this.getMazeUiState().setSpacing(spacing)
                )
        );

        helper.addChild(
                CycleButton.onOffBuilder(getMazeUiState().isInfiniteWall())
                        .withTooltip(_ -> infiniteWallTooltip)
                        .create(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                                Component.translatable("createWorld.customize.maze_world.infinite_walls"),
                                (_, infiniteWall) -> this.getMazeUiState().setInfiniteWall(infiniteWall)
                        )
        );

        helper.addChild(
                new IntegerSliderWidget(
                        0, 0, Button.DEFAULT_WIDTH,
                        Component.translatable("createWorld.customize.maze_world.threshold"),
                        (int) (getMazeUiState().getThreshold() * 100), 0, 100,
                        (slider, _) -> this.getMazeUiState().setThreshold(slider.getPercentageValue())
                )
        );

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

        this.mazePreviewWidget.preRender();
        this.stateChangeListener = state -> {
            this.mazePreviewWidget.updateConfig(((WorldCreationUIStateAccess) state).createMazeConfig());
            this.mazePreviewWidget.preRender();
        };
        this.parent.getUiState().addListener(this.stateChangeListener);
        this.stateChangeListener.accept(this.parent.getUiState());
        
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
        if(stateChangeListener != null) {
            this.getMazeUiState().removeListener(stateChangeListener);
        }
        super.removed();
    }
}
