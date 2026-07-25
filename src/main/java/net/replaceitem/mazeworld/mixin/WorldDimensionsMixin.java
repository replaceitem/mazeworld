package net.replaceitem.mazeworld.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.replaceitem.mazeworld.MazeGeneratorConfig;
import net.replaceitem.mazeworld.fakes.LevelStemAccess;
import net.replaceitem.mazeworld.fakes.WorldDimensionsAccess;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.stream.Collectors;

@Mixin(WorldDimensions.class)
public abstract class WorldDimensionsMixin implements WorldDimensionsAccess {
    @Shadow @Final private Map<ResourceKey<LevelStem>, LevelStem> dimensions;

    @Unique
    @Override
    public WorldDimensions withMazeGenerator(@Nullable MazeGeneratorConfig mazeGenerator) {
        return new WorldDimensions(this.dimensions.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), ((LevelStemAccess)(Object) entry.getValue()).withMazeGenerator(mazeGenerator)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
