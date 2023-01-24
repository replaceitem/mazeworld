package net.replaceitem.mazeworld.mixin;

import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.replaceitem.mazeworld.MazeChunkGenerator;
import net.replaceitem.mazeworld.MazeChunkGeneratorConfig;
import net.replaceitem.mazeworld.MazeWorld;
import net.replaceitem.mazeworld.fakes.DimensionOptionsRegistryHolderAccess;
import net.replaceitem.mazeworld.screen.CustomizeMazeLevelScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
@Mixin(LevelScreenProvider.class)
public interface LevelScreenProviderMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Map;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;"))
    private static <K, V> Map<K, V> insertLevelScreenProvider(K k1, V v1, K k2, V v2) {
        LevelScreenProvider factory = (parent, generatorOptionsHolder) -> {
            ChunkGenerator chunkGenerator = generatorOptionsHolder.selectedDimensions().getChunkGenerator();
            return new CustomizeMazeLevelScreen(parent,
                    mazeChunkGeneratorConfig -> ((MoreOptionsDialogAccessor) parent.moreOptionsDialog).callApply(createGlobalModifier(mazeChunkGeneratorConfig)),
                    chunkGenerator instanceof MazeChunkGenerator mazeChunkGenerator ? mazeChunkGenerator.getConfig() : MazeChunkGeneratorConfig.getDefaultConfig()
            );
        };
        return Map.of(k1, v1, k2, v2, (K) Optional.of(MazeWorld.MAZE_WORLD), (V) factory);
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createGlobalModifier(MazeChunkGeneratorConfig mazeChunkGeneratorConfig) {
        return (dynamicRegistryManager, dimensionsRegistryHolder) -> ((DimensionOptionsRegistryHolderAccess) (Object) dimensionsRegistryHolder).globalWith(dynamicRegistryManager, mazeChunkGeneratorConfig);
    }
}
