package net.replaceitem.mazeworld.screen.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeGenerator2D;

import java.util.ArrayList;
import java.util.List;

public class MazePreviewWidget implements Drawable, Element, Selectable {

    private final List<Integer> wallSpots = new ArrayList<>();
    private final int x, y;
    private final MazeChunkGeneratorConfig config;
    private double vx, vy;
    private final int w, h;

    public MazePreviewWidget(int x, int y, int w, int h, MazeChunkGeneratorConfig config) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.config = config;
    }
    
    public void preRender() {
        wallSpots.clear();
        MazeGenerator2D.BlockChecker2D blockChecker = config.mazeType.getGenerator(config).getBlockChecker(0);
        int spacing = config.spacing;
        int offsetX = (int) (vx * spacing);
        int offsetY = (int) (vy * spacing);
        for(int pixelX = 0; pixelX < w; pixelX++) {
            for(int pixelY = 0; pixelY < h; pixelY++) {
                int blockX = pixelX+offsetX-w/2;
                int blockY = pixelY+offsetY-h/2;
                if(blockChecker.isBlockAt(blockX, blockY)) {
                    wallSpots.add((pixelX & 0xFFFF) << 16 | (pixelY & 0xFFFF));
                }
            }
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        drawContext.fill(x, y, x+w, y+h, 0xFF20D020);
        for (Integer wallSpot : wallSpots) {
            int px = wallSpot >> 16;
            int py = wallSpot & 0xFFFF;
            setWall(drawContext, px, py);
        }
    }

    private void setWall(DrawContext drawContext, int px, int py) {
        int sx = x + px;
        int sy = y + py;
        drawContext.fill(sx, sy, sx+1, sy+1, 0xFF000000);
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
