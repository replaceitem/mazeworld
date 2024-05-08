package net.replaceitem.mazeworld.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator2D;

public class MazePreviewWidget implements Drawable, Element, Selectable {
    public static final Identifier ID = new Identifier("mazeworld", "preview_texture");
    public static final int DEFAULT_WALL_COLOR = 0xFF000000;

    private final NativeImage image;
    private final NativeImageBackedTexture texture;
    private final TextureManager textureManager;
    private final int x, y;
    private final MazeChunkGeneratorConfig config;
    private double vx, vy;
    private final int w, h;

    public MazePreviewWidget(int x, int y, int w, int h, MazeChunkGeneratorConfig config, TextureManager textureManager) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.textureManager = textureManager;
        this.texture = new NativeImageBackedTexture(w, h, false);
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
        int offsetX = (int) (vx * spacing) - w/2;
        int offsetY = (int) (vy * spacing) - h/2;
        for(int pixelX = 0; pixelX < w; pixelX++) {
            for(int pixelY = 0; pixelY < h; pixelY++) {
                int blockX = pixelX+offsetX;
                int blockY = pixelY+offsetY;
                image.setColor(pixelX, pixelY, blockChecker.isBlockAt(blockX, blockY) ? wallColor : backgroundColor);
            }
        }
        this.texture.upload();
        this.textureManager.registerTexture(ID, this.texture);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        drawContext.drawTexture(ID, x, y, 0.0F, 0.0F, w, h, w, h);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= (double)this.x && mouseY >= (double)this.y && mouseX < (double)(this.x + this.w) && mouseY < (double)(this.y + this.h);
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.vx -= deltaX / config.spacing;
        this.vy -= deltaY / config.spacing;
        preRender();
        return true;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, "Maze preview panel");
    }
}
