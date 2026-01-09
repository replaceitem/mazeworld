package net.replaceitem.mazeworld.screen.widget;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator2D;
import net.replaceitem.mazeworld.MazeWorld;

public class MazePreviewWidget extends AbstractWidget {
    public static final ResourceLocation ID = MazeWorld.id("preview_texture");
    public static final int DEFAULT_WALL_COLOR = 0xFF000000;

    private final NativeImage image;
    private final DynamicTexture texture;
    private final TextureManager textureManager;
    private final MazeChunkGeneratorConfig config;
    private double vx, vy;
    private boolean needsRender = true;

    public MazePreviewWidget(int x, int y, int w, int h, MazeChunkGeneratorConfig config, TextureManager textureManager) {
        super(x, y, w, h, Component.empty());
        this.textureManager = textureManager;
        this.texture = new DynamicTexture("Mazeworld Preview" ,w, h, false);
        this.image = texture.getPixels();
        this.config = config;
    }
    
    public void preRender() {
        MazeGenerator2D.BlockChecker2D blockChecker = config.mazeType.getGenerator(config).getBlockChecker(0);
        int wallColor = BuiltInRegistries.BLOCK.get(config.wallBlock)
                .map(Holder.Reference::value)
                .map(block -> block.defaultMapColor().calculateARGBColor(MapColor.Brightness.NORMAL))
                .orElse(DEFAULT_WALL_COLOR);
        int backgroundColor = Blocks.GRASS_BLOCK.defaultMapColor().calculateARGBColor(MapColor.Brightness.HIGH);
        int spacing = config.spacing;
        int offsetX = (int) (vx * spacing) - getWidth()/2;
        int offsetY = (int) (vy * spacing) - getHeight()/2;
        for(int pixelX = 0; pixelX < getWidth(); pixelX++) {
            for(int pixelY = 0; pixelY < getHeight(); pixelY++) {
                int blockX = pixelX+offsetX;
                int blockY = pixelY+offsetY;
                image.setPixel(pixelX, pixelY, blockChecker.isBlockAt(blockX, blockY) ? wallColor : backgroundColor);
            }
        }
        this.texture.upload();
        this.textureManager.register(ID, this.texture);
    }
    
    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        if(this.needsRender) {
            preRender();
            this.needsRender = false;
        }
        context.blit(RenderPipelines.GUI_TEXTURED, ID, getX(), getY(), 0.0F, 0.0F, getWidth(), getHeight(), getWidth(), getHeight());
    }
    
    @Override
    protected void onDrag(MouseButtonEvent click, double offsetX, double offsetY) {
        this.vx -= offsetX / config.spacing;
        this.vy -= offsetY / config.spacing;
        this.needsRender = true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        builder.add(NarratedElementType.TITLE, "Maze preview panel");
    }
}
