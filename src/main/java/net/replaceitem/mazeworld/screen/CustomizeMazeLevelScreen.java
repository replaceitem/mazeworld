package net.replaceitem.mazeworld.screen;

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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeType;
import net.replaceitem.mazeworld.MazeTypes;
import net.replaceitem.mazeworld.screen.widget.IntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.LogarithmicIntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.MazePreviewWidget;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class CustomizeMazeLevelScreen extends Screen {

    private static final Tooltip infiniteWallTooltip = Tooltip.create(Component.translatable("createWorld.customize.maze_world.infinite_walls.description"));
    
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    protected final CreateWorldScreen parent;
    private final MazeChunkGeneratorConfig modifiedConfig;
    private final Consumer<MazeChunkGeneratorConfig> configConsumer;
    
    public CustomizeMazeLevelScreen(CreateWorldScreen parent, Consumer<MazeChunkGeneratorConfig> configConsumer, MazeChunkGeneratorConfig config) {
        super(Component.translatable("createWorld.customize.maze_world.title"));
        this.parent = parent;
        this.configConsumer = configConsumer;
        this.modifiedConfig = config.copy();
    }

    @Nullable
    private MazePreviewWidget mazePreviewWidget;

    private void reloadPreview() {
        if(mazePreviewWidget != null) {
            mazePreviewWidget.preRender();
        }
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);
        
        int buttonWidth = 150;
        int buttonHeight = 20;
        int column1x = width/2-5-buttonWidth;
        int column2x = width/2+5;

        GridLayout gridWidget = this.layout.addToContents(new GridLayout());
        gridWidget.spacing(10);

        CycleButton.OnValueChange<MazeType> mazeTypeUpdateCallback = (_, value) -> {
            this.modifiedConfig.mazeType = value;
            reloadPreview();
        };
        
        gridWidget.addChild(
                CycleButton.builder(mazeType -> mazeType.name, modifiedConfig.mazeType)
                        .withValues(MazeTypes.types)
                        .withTooltip(mazeType1 -> Tooltip.create(mazeType1.getTooltipText()))
                        .create(0, 0, buttonWidth, buttonHeight, Component.translatable("createWorld.customize.maze_world.maze_type"), mazeTypeUpdateCallback),
                0, 0
        );

        gridWidget.addChild(
                new LogarithmicIntegerSliderWidget(0, 0, buttonWidth,
                        Component.translatable("createWorld.customize.maze_world.spacing"),
                        modifiedConfig.spacing, 2, 1024,
                        (_, value) -> {
                            modifiedConfig.spacing = value;
                            reloadPreview();
                        }
                ),
                0, 1
        );

        gridWidget.addChild(
                CycleButton.onOffBuilder(modifiedConfig.infiniteWall)
                        .withTooltip(aBoolean -> infiniteWallTooltip)
                        .create(0, 0, buttonWidth, buttonHeight,
                                Component.translatable("createWorld.customize.maze_world.infinite_walls"),
                                (button, value) -> this.modifiedConfig.infiniteWall = value
                        ),
                1, 0
        );

        gridWidget.addChild(
                new IntegerSliderWidget(
                        0, 0, buttonWidth,
                        Component.translatable("createWorld.customize.maze_world.threshold"),
                        (int) (modifiedConfig.threshold * 100), 0, 100,
                        (integerSliderWidget, value) -> {
                            modifiedConfig.threshold = integerSliderWidget.getPercentageValue();
                            reloadPreview();
                        }
                ),
                1, 1
        );

        EditBox wallBlockWidget = gridWidget.addChild(
                new EditBox(this.font, 0, 0, buttonWidth, buttonHeight, Component.empty()),
                2, 0
        );
        wallBlockWidget.setValue(modifiedConfig.wallBlock.toString());
        wallBlockWidget.setHint(Component.nullToEmpty("Maze wall block"));
        wallBlockWidget.setResponder(s -> {
            Identifier identifier = Identifier.tryParse(s);
            if (identifier != null) modifiedConfig.wallBlock = identifier;
            reloadPreview();
        });

        mazePreviewWidget = gridWidget.addChild(
                new MazePreviewWidget(
                        this.width / 2 - 10 * 16 / 2, height - 30 - 5 * 16, 160, 80,
                        modifiedConfig, this.minecraft.getTextureManager()
                ),
                3, 0, 1, 2,
                LayoutSettings::alignHorizontallyCenter
        );

        LinearLayout footerLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footerLayout.defaultCellSetting().alignVerticallyMiddle();
        footerLayout.addChild(
                Button.builder(CommonComponents.GUI_DONE, this::onDone)
                        .pos(column1x, this.height - 28)
                        .size(buttonWidth, buttonHeight).build()
        );
        footerLayout.addChild(
                Button.builder(CommonComponents.GUI_CANCEL, this::onCancel)
                        .pos(column2x, this.height - 28)
                        .size(buttonWidth, buttonHeight).build()
        );

        mazePreviewWidget.preRender();
        
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    private void onDone(Button buttonWidget) {
        this.configConsumer.accept(this.modifiedConfig);
        this.minecraft.setScreenAndShow(this.parent);
    }

    private void onCancel(Button buttonWidget) {
        this.minecraft.setScreenAndShow(this.parent);
    }
}
