package net.replaceitem.mazeworld.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeType;
import net.replaceitem.mazeworld.MazeTypes;
import net.replaceitem.mazeworld.screen.widget.IntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.LogarithmicIntegerSliderWidget;
import net.replaceitem.mazeworld.screen.widget.MazePreviewWidget;

import java.util.function.Consumer;

public class CustomizeMazeLevelScreen extends Screen {

    private static final Tooltip infiniteWallTooltip = Tooltip.of(Text.translatable("createWorld.customize.maze_world.infinite_walls.description"));
    
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    protected final CreateWorldScreen parent;
    private final MazeChunkGeneratorConfig modifiedConfig;
    private final Consumer<MazeChunkGeneratorConfig> configConsumer;
    
    public CustomizeMazeLevelScreen(CreateWorldScreen parent, Consumer<MazeChunkGeneratorConfig> configConsumer, MazeChunkGeneratorConfig config) {
        super(Text.translatable("createWorld.customize.maze_world.title"));
        this.parent = parent;
        this.configConsumer = configConsumer;
        this.modifiedConfig = config.copy();
    }

    private MazePreviewWidget mazePreviewWidget;

    @Override
    protected void init() {
        this.layout.addHeader(this.title, this.textRenderer);
        
        int buttonWidth = 150;
        int buttonHeight = 20;
        int column1x = width/2-5-buttonWidth;
        int column2x = width/2+5;

        GridWidget gridWidget = this.layout.addBody(new GridWidget());
        gridWidget.setSpacing(10);

        CyclingButtonWidget.UpdateCallback<MazeType> mazeTypeUpdateCallback = (button, value) -> {
            this.modifiedConfig.mazeType = value;
            mazePreviewWidget.preRender();
        };
        
        gridWidget.add(
                CyclingButtonWidget.<MazeType>builder(mazeType -> mazeType.name)
                        .values(MazeTypes.types)
                        .initially(modifiedConfig.mazeType)
                        .tooltip(mazeType1 -> Tooltip.of(mazeType1.getTooltipText()))
                        .build(0, 0, buttonWidth, buttonHeight, Text.translatable("createWorld.customize.maze_world.maze_type"), mazeTypeUpdateCallback),
                0, 0
        );

        gridWidget.add(
                new LogarithmicIntegerSliderWidget(0, 0, buttonWidth,
                        Text.translatable("createWorld.customize.maze_world.spacing"),
                        modifiedConfig.spacing, 2, 1024,
                        (integerSliderWidget, value) -> {
                            modifiedConfig.spacing = value;
                            mazePreviewWidget.preRender();
                        }
                ),
                0, 1
        );

        gridWidget.add(
                CyclingButtonWidget.onOffBuilder(modifiedConfig.infiniteWall)
                        .tooltip(aBoolean -> infiniteWallTooltip)
                        .build(0, 0, buttonWidth, buttonHeight,
                                Text.translatable("createWorld.customize.maze_world.infinite_walls"),
                                (button, value) -> this.modifiedConfig.infiniteWall = value
                        ),
                1, 0
        );

        gridWidget.add(
                new IntegerSliderWidget(
                        0, 0, buttonWidth,
                        Text.translatable("createWorld.customize.maze_world.threshold"),
                        (int) (modifiedConfig.threshold * 100), 0, 100,
                        (integerSliderWidget, value) -> {
                            modifiedConfig.threshold = integerSliderWidget.getPercentageValue();
                            mazePreviewWidget.preRender();
                        }
                ),
                1, 1
        );

        TextFieldWidget wallBlockWidget = gridWidget.add(
                new TextFieldWidget(this.textRenderer, 0, 0, buttonWidth, buttonHeight, Text.empty()),
                2, 0
        );
        wallBlockWidget.setText(modifiedConfig.wallBlock.toString());
        wallBlockWidget.setPlaceholder(Text.of("Maze wall block"));
        wallBlockWidget.setChangedListener(s -> {
            Identifier identifier = Identifier.tryParse(s);
            if (identifier != null) modifiedConfig.wallBlock = identifier;
            mazePreviewWidget.preRender();
        });

        assert this.client != null;
        mazePreviewWidget = gridWidget.add(
                new MazePreviewWidget(
                        this.width / 2 - 10 * 16 / 2, height - 30 - 5 * 16, 160, 80,
                        modifiedConfig, this.client.getTextureManager()
                ),
                3, 0, 1, 2,
                Positioner::alignHorizontalCenter
        );

        DirectionalLayoutWidget footerLayout = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        footerLayout.getMainPositioner().alignVerticalCenter();
        footerLayout.add(
                ButtonWidget.builder(ScreenTexts.DONE, this::onDone)
                        .position(column1x, this.height - 28)
                        .size(buttonWidth, buttonHeight).build()
        );
        footerLayout.add(
                ButtonWidget.builder(ScreenTexts.CANCEL, this::onCancel)
                        .position(column2x, this.height - 28)
                        .size(buttonWidth, buttonHeight).build()
        );

        mazePreviewWidget.preRender();
        
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    private void onDone(ButtonWidget buttonWidget) {
        if(this.client == null) return; // shouldn't happen
        this.configConsumer.accept(this.modifiedConfig);
        this.client.setScreen(this.parent);
    }

    private void onCancel(ButtonWidget buttonWidget) {
        if(this.client == null) return; // shouldn't happen
        this.client.setScreen(this.parent);
    }
}
