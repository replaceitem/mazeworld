package net.replaceitem.mazeworld.screen.widget;

import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeType;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class MazePreviewWidget extends DrawableHelper implements Drawable, Element {

    private final List<Integer> wallSpots = new ArrayList<>();
    private final int x, y;
    private final int w, h;
    private final int chunksW, chunksH;

    public MazePreviewWidget(int x, int y, int chunksW, int chunksH) {
        this.x = x;
        this.y = y;
        this.chunksW = chunksW;
        this.chunksH = chunksH;
        this.w = chunksW<<4;
        this.h = chunksH<<4;
    }
    
    public void preRender(MazeChunkGeneratorConfig config) {
        wallSpots.clear();
        for(int cx = 0; cx < chunksW; cx++) {
            for(int cy = 0; cy < chunksH; cy++) {
                MazeType.BlockChecker blockChecker = config.mazeType.getBlockChecker(new ChunkPos(cx, cy), config, 0);
                int chunkOriginX = cx<<4;
                int chunkOriginY = cy<<4;
                for(int bx = 0; bx < 16; bx++) {
                    for(int by = 0; by < 16; by++) {
                        int blockX = chunkOriginX+bx;
                        int blockY = chunkOriginY+by;
                        if(blockChecker.isBlockAt(blockX, blockY)) {
                            wallSpots.add((blockX & 0xFFFF) << 16 | (blockY & 0xFFFF));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, x, y, x+w, y+h, 0xFF20D020);
        for (Integer wallSpot : wallSpots) {
            int px = wallSpot >> 16;
            int py = wallSpot & 0xFFFF;
            setWall(matrices, px, py);
        }
    }

    private void setWall(MatrixStack matrices, int px, int py) {
        int sx = x + px;
        int sy = y + py;
        fill(matrices, sx, sy, sx+1, sy+1, 0xFF000000);
    }
}
