package net.replaceitem.mazeworld.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.replaceitem.mazeworld.config.MazeGeneratorConfig;
import net.replaceitem.mazeworld.RecordRecoderRegistration;
import net.replaceitem.mazeworld.fakes.LevelStemAccess;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.BiFunction;

@Mixin(LevelStem.class)
public class LevelStemMixin implements LevelStemAccess {
    @Shadow @Final private Holder<DimensionType> type;
    @Shadow @Final private ChunkGenerator generator;

    // simply adds a field maze_generator to the codec
    @WrapOperation(method = "lambda$static$0", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/Products$P2;apply(Lcom/mojang/datafixers/kinds/Applicative;Lcom/mojang/datafixers/kinds/App;)Lcom/mojang/datafixers/kinds/App;"))
    private static App<RecordCodecBuilder.Mu<LevelStem>, LevelStem> addField(
            Products.P2<RecordCodecBuilder.Mu<LevelStem>, Holder<DimensionType>, ChunkGenerator> it,
            Applicative<RecordCodecBuilder.Mu<LevelStem>, ?> instance,
            App<RecordCodecBuilder.Mu<LevelStem>, BiFunction<Holder<DimensionType>, ChunkGenerator, LevelStem>> function,
            Operation<App<RecordCodecBuilder.Mu<LevelStem>, LevelStem>> original,
            @Local(name = "i", argsOnly = true) RecordCodecBuilder.Instance<LevelStem> i
    ) {
        return it.and(MazeGeneratorConfig.CODEC.optionalFieldOf("maze_generator").forGetter((key) -> Optional.ofNullable(((LevelStemAccess)(Object) key).getMazeGenerator())))
                .apply(i, i.stable((dimensionTypeHolder, chunkGenerator, mazeChunkGenerator) ->
                        LevelStemAccess.createWithMazeGenerator(dimensionTypeHolder, chunkGenerator, mazeChunkGenerator.orElse(null))
                ));
    }

    @Override
    public @Nullable MazeGeneratorConfig getMazeGenerator() {
        return RecordRecoderRegistration.LEVEL_STEM_MAZE_GENERATOR_KEY.getOrNull(((LevelStem)(Object) this));
    }

    @Override
    public LevelStem withMazeGenerator(@Nullable MazeGeneratorConfig mazeGenerator) {
        return LevelStemAccess.createWithMazeGenerator(this.type, this.generator, mazeGenerator);
    }
}
