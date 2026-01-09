package net.replaceitem.mazeworld;

import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MazeType {

    private final Function<MazeChunkGeneratorConfig, MazeGenerator<?>> constructor;

    public MazeType(String id, Function<MazeChunkGeneratorConfig, MazeGenerator<?>> constructor) {
        this.id = id;
        this.name = Component.translatable("maze_type." + id + ".name");
        this.description = Component.translatable("maze_type." + id + ".description");
        this.constructor = constructor;
        this.tooltipText = this.name.copy().withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD).append("\n").append(this.description.copy());
    }
    public final String id;
    public final MutableComponent name;
    public final MutableComponent description;
    public final Component tooltipText;
    
    public Component getTooltipText() {
        return tooltipText;
    }

    public MazeGenerator<?> getGenerator(MazeChunkGeneratorConfig config) {
        return constructor.apply(config);
    }
}
