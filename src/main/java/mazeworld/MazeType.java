package mazeworld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.List;
import java.util.Random;

public abstract class MazeType {
    
    public MazeType(String id) {
        this.id = id;
        this.name = Text.translatable("maze_type." + id + ".name");
        this.description = Text.translatable("maze_type." + id + ".description");
        this.tooltip = StringVisitable.concat(this.name.copy().formatted(Formatting.BOLD, Formatting.GOLD),Text.literal("\n"),this.description.copy());
    }
    
    public final String id;
    public final MutableText name;
    public final MutableText description;
    public final StringVisitable tooltip;
    
    public List<OrderedText> getTooltip() {
        return MinecraftClient.getInstance().textRenderer.wrapLines(this.tooltip,100);
    }

    public void generateChunk(MazeChunkGeneratorConfig config, StructureWorldAccess world, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        long worldSeed = world.getSeed();
        int xs = chunkPos.getStartX();
        int zs = chunkPos.getStartZ();
        int xe = chunkPos.getEndX();
        int ze = chunkPos.getEndZ();

        BlockChecker blockChecker = getBlockChecker(chunkPos, config, worldSeed);
        
        boolean hasCeiling = world.getDimension().hasCeiling();
        
        if(!hasCeiling) fillPlane(chunk, world.getTopY()-1,Blocks.BARRIER.getDefaultState());
        
        int wallHeight = world.getDimension().logicalHeight();
        
        for(int i = xs; i <= xe; i++) {
            for(int j = zs; j <= ze; j++) {
                if(blockChecker.isBlockAt(i, j))
                    placeColumn(world, chunk, i, j, wallHeight, Blocks.BEDROCK.getDefaultState());
            }
        }
        
    }
    
    protected static void placeColumn(StructureWorldAccess world, Chunk chunk, int cx, int cz, int top, BlockState blockState) {
        BlockPos.Mutable pos = new BlockPos.Mutable(cx, world.getBottomY(), cz);
        while(pos.getY() < top) {
            setBlock(chunk, pos, blockState);
            pos.setY(pos.getY()+1);
        }
    }

    protected static void fillPlane(Chunk chunk, int y, BlockState blockState) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                BlockPos pos = new BlockPos(i, y, j);
                setBlock(chunk, pos, blockState);
            }
        }
    }
    
    protected static void setBlock(Chunk chunk, BlockPos pos, BlockState blockState) {
        chunk.setBlockState(pos, blockState, false);
        chunk.removeBlockEntity(pos);
    }
    
    public abstract BlockChecker getBlockChecker(ChunkPos chunkPos, MazeChunkGeneratorConfig config, long seed);

    @FunctionalInterface
    public interface BlockChecker {
        boolean isBlockAt(int x, int y);
    }
    
    public static Random getMultiSeededRandom(long seed, int... ints) {
        for (int num : ints) {
            seed = new Random(seed+num).nextLong();
        }
        return new Random(seed);
    }

    public static int getRandomIntAt(int x, int y, long seed, int max) {
        return Math.abs(getMultiSeededRandom(seed, x, y).nextInt(max));
    }
}
