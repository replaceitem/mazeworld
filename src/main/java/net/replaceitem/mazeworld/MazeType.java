package net.replaceitem.mazeworld;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Function;

public class MazeType {

    private final Function<MazeChunkGeneratorConfig, MazeGenerator<?>> constructor;

    public MazeType(String id, Function<MazeChunkGeneratorConfig, MazeGenerator<?>> constructor) {
        this.id = id;
        this.name = Text.translatable("maze_type." + id + ".name");
        this.description = Text.translatable("maze_type." + id + ".description");
        this.constructor = constructor;
        this.tooltipText = this.name.copy().formatted(Formatting.BOLD, Formatting.GOLD).append("\n").append(this.description.copy());
    }
    public final String id;
    public final MutableText name;
    public final MutableText description;
    public final Text tooltipText;
    
    public Text getTooltipText() {
        return tooltipText;
    }

    public MazeGenerator<?> getGenerator(MazeChunkGeneratorConfig config) {
        return constructor.apply(config);
    }
}
