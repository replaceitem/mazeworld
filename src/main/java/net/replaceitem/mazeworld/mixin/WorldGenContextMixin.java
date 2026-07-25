package net.replaceitem.mazeworld.mixin;

import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Needed for {@link net.replaceitem.mazeworld.RecordRecoderRegistration#WORLD_GEN_CONTEXT_MAZE_GENERATOR}
 */
@Mixin(WorldGenContext.class)
public class WorldGenContextMixin {

}
