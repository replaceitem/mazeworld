package net.replaceitem.mazeworld.screen.widget;

import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator2D;
import net.replaceitem.mazeworld.MazeWorld;

public class MazePreviewWidget extends ClickableWidget {
    public static final Identifier ID = MazeWorld.id("preview_texture");
    public static final int DEFAULT_WALL_COLOR = 0xFF000000;

    private final NativeImage image;
    private final NativeImageBackedTexture texture;
    private final TextureManager textureManager;
    private final MazeChunkGeneratorConfig config;
    private double vx, vy;
    private boolean needsRender = true;

    public MazePreviewWidget(int x, int y, int w, int h, MazeChunkGeneratorConfig config, TextureManager textureManager) {
        super(x, y, w, h, Text.empty());
        this.textureManager = textureManager;
        this.texture = new NativeImageBackedTexture("Mazeworld Preview" ,w, h, false);
        this.image = texture.getImage();
        this.config = config;
    }
    
    public void preRender() {
        MazeGenerator2D.BlockChecker2D blockChecker = config.mazeType.getGenerator(config).getBlockChecker(0);
        int wallColor = Registries.BLOCK.getEntry(config.wallBlock)
                .map(RegistryEntry.Reference::value)
                .map(block -> block.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL))
                .orElse(DEFAULT_WALL_COLOR);
        int backgroundColor = Blocks.GRASS_BLOCK.getDefaultMapColor().getRenderColor(MapColor.Brightness.HIGH);
        int spacing = config.spacing;
        int offsetX = (int) (vx * spacing) - getWidth()/2;
        int offsetY = (int) (vy * spacing) - getHeight()/2;
        for(int pixelX = 0; pixelX < getWidth(); pixelX++) {
            for(int pixelY = 0; pixelY < getHeight(); pixelY++) {
                int blockX = pixelX+offsetX;
                int blockY = pixelY+offsetY;
                image.setColorArgb(pixelX, pixelY, blockChecker.isBlockAt(blockX, blockY) ? wallColor : backgroundColor);
            }
        }
        this.texture.upload();
        this.textureManager.registerTexture(ID, this.texture);
    }
    
    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if(this.needsRender) {
            preRender();
            this.needsRender = false;
        }
        context.drawTexture(RenderLayer::getGuiTextured, ID, getX(), getY(), 0.0F, 0.0F, getWidth(), getHeight(), getWidth(), getHeight());
    }


    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        this.vx -= deltaX / config.spacing;
        this.vy -= deltaY / config.spacing;
        this.needsRender = true;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, "Maze preview panel");
    }
}
