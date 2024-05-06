package net.replaceitem.mazeworld.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGenerators;
import net.replaceitem.mazeworld.MazeChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerators.class)
public class ChunkGeneratorsMixin {
    @Inject(method = "registerAndGetDefault", at = @At("RETURN"))
    private static void registerMazeGenerator(Registry<MapCodec<? extends ChunkGenerator>> registry, CallbackInfoReturnable<MapCodec<? extends ChunkGenerator>> cir) {
        Registry.register(registry, new Identifier("mazeworld", "maze_world"), MazeChunkGenerator.CODEC);
    }
}
