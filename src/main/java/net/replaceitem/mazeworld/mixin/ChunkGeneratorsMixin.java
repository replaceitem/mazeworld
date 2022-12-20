package net.replaceitem.mazeworld.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGenerators;
import net.replaceitem.mazeworld.MazeChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkGenerators.class)
public class ChunkGeneratorsMixin {
    @Inject(method = "registerAndGetDefault", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void registerMazeGenerator(Registry<Codec<? extends ChunkGenerator>> registry, CallbackInfoReturnable<Codec<? extends ChunkGenerator>> cir) {
        Registry.register(registry, new Identifier("mazeworld", "maze_world"), MazeChunkGenerator.CODEC);
    }
}
