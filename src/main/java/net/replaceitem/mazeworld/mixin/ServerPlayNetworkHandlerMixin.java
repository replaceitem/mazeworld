package net.replaceitem.mazeworld.mixin;

import net.replaceitem.mazeworld.ServerWorldAccess;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public abstract void requestTeleport(double x, double y, double z, float yaw, float pitch);

    @Shadow public ServerPlayerEntity player;

    @Shadow @Final public ClientConnection connection;
    
    private boolean inWallPreviously = false;

    @Inject(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getBoundingBox()Lnet/minecraft/util/math/Box;"))
    private void verifyMovement(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if(!packet.changesPosition()) return;
        if(!this.player.interactionManager.getGameMode().isSurvivalLike()) return;
        World world = this.player.world;
        if(!((ServerWorldAccess) world).isInfiniteMaze()) return;
        if(world.getDimension().hasCeiling()) return;

        double x = packet.getX(this.player.getX());
        double y = packet.getY(this.player.getY());
        double z = packet.getZ(this.player.getZ());
        Box box = this.player.getDimensions(this.player.getPose()).getBoxAt(x, y, z);
        boolean inWall = isInsideWall(box);
        if(inWall && !inWallPreviously) {
            this.requestTeleport(this.player.getX(), y, this.player.getZ(), packet.getYaw(this.player.getYaw()), packet.getPitch(this.player.getPitch()));
        }
        inWallPreviously = inWall;
    }
    
    @Inject(method = "onVehicleMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isSpaceEmpty(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z"))
    private void verifyVehicleMovement(VehicleMoveC2SPacket packet, CallbackInfo ci) {
        if(!this.player.interactionManager.getGameMode().isSurvivalLike()) return;
        World world = this.player.world;
        if(!((ServerWorldAccess) world).isInfiniteMaze()) return;
        if(world.getDimension().hasCeiling()) return;

        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        Entity rootVehicle = this.player.getRootVehicle();
        Box box = rootVehicle.getDimensions(rootVehicle.getPose()).getBoxAt(x, y, z);
        
        boolean inWall = isInsideWall(box);
        if(inWall && !inWallPreviously) {
            this.connection.send(new VehicleMoveS2CPacket(rootVehicle));
        }
        inWallPreviously = inWall;
    }

    private boolean isInsideWall(Box box) {
        double maxY = this.player.world.getTopY()-1;
        BlockPos blockPos = new BlockPos(box.minX + 0.001, maxY, box.minZ + 0.001);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001, maxY, box.maxZ - 0.001);
        if (this.player.world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int blockPosX = blockPos.getX(); blockPosX <= blockPos2.getX(); ++blockPosX) {
                for (int blockPosZ = blockPos.getZ(); blockPosZ <= blockPos2.getZ(); ++blockPosZ) {
                    mutable.set(blockPosX, maxY, blockPosZ);
                    BlockState blockState = this.player.world.getBlockState(mutable);
                    if(blockState.getBlock() == Blocks.BEDROCK) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
