package net.replaceitem.mazeworld.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.RaycastContext;
import net.replaceitem.mazeworld.MazeCollisionView;
import net.replaceitem.mazeworld.fakes.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CollisionView.class)
public interface CollisionViewMixin extends BlockView {
    @ModifyReceiver(method = "getCollisionsIncludingWorldBorder", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/CollisionView;raycast(Lnet/minecraft/world/RaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;"))
    private CollisionView wrapCollisionRaycast(CollisionView instance, RaycastContext raycastContext) {
        if(this instanceof ServerWorld serverWorld && ((ServerWorldAccess) serverWorld).isInfiniteMaze()) {
            return new MazeCollisionView(instance, ((ServerWorldAccess) serverWorld).getMazeWallBlock());
        }
        return instance;
    }
}
