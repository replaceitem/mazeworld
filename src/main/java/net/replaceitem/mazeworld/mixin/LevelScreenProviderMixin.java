package net.replaceitem.mazeworld.mixin;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.replaceitem.mazeworld.MazeChunkGenerator;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeWorld;
import net.replaceitem.mazeworld.fakes.DimensionOptionsRegistryHolderAccess;
import net.replaceitem.mazeworld.screen.CustomizeMazeLevelScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
@Mixin(PresetEditor.class)
public interface LevelScreenProviderMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Map;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;"))
    private static <K, V> Map<K, V> insertLevelScreenProvider(K k1, V v1, K k2, V v2) {
        PresetEditor factory = (parent, generatorOptionsHolder) -> {
            ChunkGenerator chunkGenerator = generatorOptionsHolder.selectedDimensions().overworld();
            return new CustomizeMazeLevelScreen(parent,
                    mazeChunkGeneratorConfig -> parent.getUiState().updateDimensions(createGlobalModifier(mazeChunkGeneratorConfig)),
                    chunkGenerator instanceof MazeChunkGenerator mazeChunkGenerator ? mazeChunkGenerator.getConfig() : MazeChunkGeneratorConfig.getDefaultConfig()
            );
        };
        return Map.of(k1, v1, k2, v2, (K) Optional.of(MazeWorld.MAZE_WORLD), (V) factory);
    }

    @Unique
    private static WorldCreationContext.DimensionsUpdater createGlobalModifier(MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        return (dynamicRegistryManager, dimensionsRegistryHolder) -> ((DimensionOptionsRegistryHolderAccess) (Object) dimensionsRegistryHolder).globalWith(dynamicRegistryManager, mazeChunkGeneratorConfig);
    }
}
