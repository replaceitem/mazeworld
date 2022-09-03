package mazeworld.screen;

import mazeworld.MazeType;
import mazeworld.MazeTypes;
import mazeworld.screen.widget.IntegerSliderWidget;
import mazeworld.screen.widget.LogarithmicIntegerSliderWidget;
import mazeworld.MazeChunkGeneratorConfig;
import mazeworld.screen.widget.MazePreviewWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class CustomizeMazeLevelScreen extends Screen {
    
    protected final CreateWorldScreen parent;
    private final MazeChunkGeneratorConfig config;
    private final MazeChunkGeneratorConfig modifiedConfig;
    private final Consumer<MazeChunkGeneratorConfig> configConsumer;
    
    public CustomizeMazeLevelScreen(CreateWorldScreen parent, Consumer<MazeChunkGeneratorConfig> configConsumer, MazeChunkGeneratorConfig config) {
        super(Text.translatable("createWorld.customize.maze_world.title"));
        this.parent = parent;
        this.configConsumer = configConsumer;
        this.config = config;
        this.modifiedConfig = config.copy();
    }
    
    private LogarithmicIntegerSliderWidget spacingWidget;
    private CyclingButtonWidget<MazeType> mazeTypeWidget;
    private CyclingButtonWidget<Boolean> infiniteWallWidget;
    private MazePreviewWidget mazePreviewWidget;

    @Override
    protected void init() {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int column1x = width/2-5-buttonWidth;
        int column2x = width/2+5;

        CyclingButtonWidget.UpdateCallback<MazeType> mazeTypeUpdateCallback = (button, value) -> {
            this.modifiedConfig.mazeType = value;
            mazePreviewWidget.preRender(modifiedConfig);
        };
        mazeTypeWidget = CyclingButtonWidget.<MazeType>builder(mazeType -> mazeType.name)
                .values(MazeTypes.types)
                .initially(modifiedConfig.mazeType)
                .tooltip(MazeType::getTooltip)
                .build(column1x, 20, buttonWidth, buttonHeight, Text.translatable("createWorld.customize.maze_world.maze_type"), mazeTypeUpdateCallback);
        this.addDrawableChild(mazeTypeWidget);

        
        
        
        IntegerSliderWidget.UpdateCallback spacingUpdateCallback = (integerSliderWidget, value) -> {
            modifiedConfig.spacing = value;
            mazePreviewWidget.preRender(modifiedConfig);
        };
        spacingWidget = new LogarithmicIntegerSliderWidget(column2x, 20, buttonWidth, Text.translatable("createWorld.customize.maze_world.spacing"), modifiedConfig.spacing, 2, 1024, spacingUpdateCallback);
        this.addDrawableChild(spacingWidget);


        CyclingButtonWidget.UpdateCallback<Boolean> infiniteWallUpdateCallback = (button, value) -> this.modifiedConfig.infiniteWall = value;
        infiniteWallWidget = CyclingButtonWidget.onOffBuilder(modifiedConfig.infiniteWall)
                .tooltip(aBoolean -> MinecraftClient.getInstance().textRenderer.wrapLines(Text.translatable("createWorld.customize.maze_world.infinite_walls.description"),100))
                .build(column1x, 60, buttonWidth, buttonHeight, Text.translatable("createWorld.customize.maze_world.infinite_walls"), infiniteWallUpdateCallback);
        this.addDrawableChild(infiniteWallWidget);
        
        
        
        mazePreviewWidget = new MazePreviewWidget(this.width / 2 - 10*16/2, height-30-5*16, 10, 5);

        
        
        this.addDrawableChild(new ButtonWidget(column1x, this.height - 28, buttonWidth, buttonHeight, ScreenTexts.DONE, button -> {
            if(this.client == null) return; // shouldn't happen
            this.configConsumer.accept(this.modifiedConfig);
            this.client.setScreen(this.parent);
        }));
        this.addDrawableChild(new ButtonWidget(column2x, this.height - 28, buttonWidth, buttonHeight, ScreenTexts.CANCEL, button -> {
            if(this.client == null) return; // shouldn't happen
            this.client.setScreen(this.parent);
        }));

        mazePreviewWidget.preRender(modifiedConfig);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);

        mazePreviewWidget.render(matrices, mouseX, mouseY, delta);
        
        for (Element child : this.children()) {
            if(child instanceof OrderableTooltip orderableTooltip && child instanceof ClickableWidget clickableWidget && clickableWidget.isHovered()) {
                List<OrderedText> tooltip = orderableTooltip.getOrderedTooltip();
                this.renderOrderedTooltip(matrices, tooltip, mouseX, mouseY);
            }
        }
    }
}
