package mazeworld.screen;

import mazeworld.MazeType;
import mazeworld.MazeTypes;
import mazeworld.screen.widget.LogarithmicIntegerSliderWidget;
import mazeworld.MazeChunkGeneratorConfig;
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
import net.minecraft.util.math.ChunkPos;

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

    @Override
    protected void init() {
        spacingWidget = new LogarithmicIntegerSliderWidget(width/2-100, 80, 200, Text.translatable("createWorld.customize.maze_world.spacing"), config.spacing, 2, 1024, (integerSliderWidget, value) -> modifiedConfig.spacing = value);
        this.addDrawableChild(spacingWidget);
        CyclingButtonWidget.Builder<MazeType> builder = new CyclingButtonWidget.Builder<>(mazeType -> mazeType.name);
        builder.values(MazeTypes.types);
        builder.initially(config.mazeType);
        builder.tooltip(MazeType::getTooltip);
        mazeTypeWidget = builder.build(width/2-100, 50, 200, 20, Text.translatable("createWorld.customize.maze_world.maze_type"), (button, value) -> this.modifiedConfig.mazeType = value);
        this.addDrawableChild(mazeTypeWidget);

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, button -> {
            if(this.client == null) return; // shouldn't happen
            config.spacing = spacingWidget.getIntegerValue();
            config.mazeType = mazeTypeWidget.getValue();
            this.configConsumer.accept(this.config);
            this.client.setScreen(this.parent);
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> {
            if(this.client == null) return; // shouldn't happen
            this.client.setScreen(this.parent);
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);

        drawMazePreview(matrices, width/2 - 80, 120, 10, 5);

        for (Element child : this.children()) {
            if(child instanceof OrderableTooltip orderableTooltip && child instanceof ClickableWidget clickableWidget && clickableWidget.isHovered()) {
                List<OrderedText> tooltip = orderableTooltip.getOrderedTooltip();
                this.renderOrderedTooltip(matrices, tooltip, mouseX, mouseY);
            }
        }
    }
    
    public void drawMazePreview(MatrixStack matrices, int x, int y, int cw, int ch) {
        for(int cx = 0; cx < cw; cx++) {
            for(int cy = 0; cy < ch; cy++) {
                MazeType.BlockChecker blockChecker = this.modifiedConfig.mazeType.getBlockChecker(new ChunkPos(cx, cy), this.modifiedConfig, 0);
                int chunkOriginX = cx<<4;
                int chunkOriginY = cy<<4;
                for(int bx = 0; bx < 16; bx++) {
                    for(int by = 0; by < 16; by++) {
                        int blockX = chunkOriginX+bx;
                        int blockY = chunkOriginY+by;
                        int color = blockChecker.isBlockAt(blockX, blockY) ? 0xFF000000 : 0xFF20D020;
                        setPixel(matrices, x+blockX, y+blockY, color);
                    }
                }
            }
        }
    }
    
    private static void setPixel(MatrixStack matrices, int x, int y, int color) {
        fill(matrices, x, y, x+1, y+1, color);
    }
}
