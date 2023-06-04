package net.replaceitem.mazeworld.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
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
    
    protected final CreateWorldScreen parent;
    private final MazeChunkGeneratorConfig modifiedConfig;
    private final Consumer<MazeChunkGeneratorConfig> configConsumer;
    
    public CustomizeMazeLevelScreen(CreateWorldScreen parent, Consumer<MazeChunkGeneratorConfig> configConsumer, MazeChunkGeneratorConfig config) {
        super(Text.translatable("createWorld.customize.maze_world.title"));
        this.parent = parent;
        this.configConsumer = configConsumer;
        this.modifiedConfig = config.copy();
    }
    
    private CyclingButtonWidget<MazeType> mazeTypeWidget;
    private LogarithmicIntegerSliderWidget spacingWidget;
    private CyclingButtonWidget<Boolean> infiniteWallWidget;
    private IntegerSliderWidget thresholdWidget;
    private TextFieldWidget wallBlockWidget;
    private MazePreviewWidget mazePreviewWidget;

    @Override
    protected void init() {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int column1x = width/2-5-buttonWidth;
        int column2x = width/2+5;

        GridWidget gridWidget = new GridWidget(0, 20);
        gridWidget.setSpacing(10);

        CyclingButtonWidget.UpdateCallback<MazeType> mazeTypeUpdateCallback = (button, value) -> {
            this.modifiedConfig.mazeType = value;
            mazePreviewWidget.preRender();
        };
        mazeTypeWidget = CyclingButtonWidget.<MazeType>builder(mazeType -> mazeType.name)
                .values(MazeTypes.types)
                .initially(modifiedConfig.mazeType)
                .tooltip(MazeType::getTooltip)
                .build(0, 0, buttonWidth, buttonHeight, Text.translatable("createWorld.customize.maze_world.maze_type"), mazeTypeUpdateCallback);
        gridWidget.add(mazeTypeWidget, 0, 0);

        
        
        
        IntegerSliderWidget.UpdateCallback spacingUpdateCallback = (integerSliderWidget, value) -> {
            modifiedConfig.spacing = value;
            mazePreviewWidget.preRender();
        };
        spacingWidget = new LogarithmicIntegerSliderWidget(0, 0, buttonWidth, Text.translatable("createWorld.customize.maze_world.spacing"), modifiedConfig.spacing, 2, 1024, spacingUpdateCallback);
        gridWidget.add(spacingWidget, 0, 1);


        CyclingButtonWidget.UpdateCallback<Boolean> infiniteWallUpdateCallback = (button, value) -> this.modifiedConfig.infiniteWall = value;
        infiniteWallWidget = CyclingButtonWidget.onOffBuilder(modifiedConfig.infiniteWall)
                .tooltip(aBoolean -> infiniteWallTooltip)
                .build(0, 0, buttonWidth, buttonHeight, Text.translatable("createWorld.customize.maze_world.infinite_walls"), infiniteWallUpdateCallback);
        gridWidget.add(infiniteWallWidget, 1, 0);

        IntegerSliderWidget.UpdateCallback thresholdUpdateCallback = (integerSliderWidget, value) -> {
            modifiedConfig.threshold = integerSliderWidget.getPercentageValue();
            mazePreviewWidget.preRender();
        };
        thresholdWidget = new IntegerSliderWidget(0, 0, buttonWidth, Text.translatable("createWorld.customize.maze_world.threshold"), (int) (modifiedConfig.threshold*100), 0, 100, thresholdUpdateCallback);
        gridWidget.add(thresholdWidget, 1, 1);

        wallBlockWidget = new TextFieldWidget(this.textRenderer, 0, 0, buttonWidth, buttonHeight, Text.empty());
        wallBlockWidget.setText(modifiedConfig.wallBlock.toString());
        wallBlockWidget.setPlaceholder(Text.of("Maze wall block"));
        wallBlockWidget.setChangedListener(s -> {
            Identifier identifier = Identifier.tryParse(s);
            if(identifier != null) modifiedConfig.wallBlock = identifier;
        });
        gridWidget.add(wallBlockWidget, 2, 0);

        gridWidget.refreshPositions();
        gridWidget.setX(width/2 - gridWidget.getWidth()/2);
        gridWidget.forEachChild(this::addDrawableChild);
        
        mazePreviewWidget = new MazePreviewWidget(this.width / 2 - 10*16/2, height-30-5*16, 160, 80, modifiedConfig);
        this.addDrawableChild(mazePreviewWidget);
        
        
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, this::onDone)
                .position(column1x, this.height - 28)
                .size(buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, this::onCancel)
                .position(column2x, this.height - 28)
                .size(buttonWidth, buttonHeight).build());

        mazePreviewWidget.preRender();
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

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(drawContext, mouseX, mouseY, delta);
        mazePreviewWidget.render(drawContext, mouseX, mouseY, delta);
    }
}
