package net.replaceitem.mazeworld.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.replaceitem.mazeworld.fakes.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandler {
    public ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Shadow public abstract void requestTeleport(double x, double y, double z, float yaw, float pitch);

    @Shadow public ServerPlayerEntity player;
    
    @Unique private boolean inWallPreviously = false;

    @Inject(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getBoundingBox()Lnet/minecraft/util/math/Box;"), cancellable = true)
    private void verifyMovement(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if(!packet.changesPosition()) return;
        if(!this.player.interactionManager.getGameMode().isSurvivalLike()) return;
        World world = this.player.getWorld();
        if(!((ServerWorldAccess) world).isInfiniteMaze()) return;
        if(world.getDimension().hasCeiling()) return;

        double x = packet.getX(this.player.getX());
        double y = packet.getY(this.player.getY());
        double z = packet.getZ(this.player.getZ());
        Box box = this.player.getDimensions(this.player.getPose()).getBoxAt(x, y, z);
        
        boolean inWall = shouldRejectMovement(box, world);
        boolean setback = inWall && !inWallPreviously;
        inWallPreviously = inWall;
        if(setback) {
            this.requestTeleport(this.player.getX(), y, this.player.getZ(), packet.getYaw(this.player.getYaw()), packet.getPitch(this.player.getPitch()));
            ci.cancel();
        }
    }
    
    @Inject(method = "onVehicleMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isSpaceEmpty(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z"), cancellable = true)
    private void verifyVehicleMovement(VehicleMoveC2SPacket packet, CallbackInfo ci) {
        if(!this.player.interactionManager.getGameMode().isSurvivalLike()) return;
        World world = this.player.getWorld();
        if(!((ServerWorldAccess) world).isInfiniteMaze()) return;
        if(world.getDimension().hasCeiling()) return;

        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        Entity rootVehicle = this.player.getRootVehicle();
        Box box = rootVehicle.getDimensions(rootVehicle.getPose()).getBoxAt(x, y, z);
        
        boolean inWall = shouldRejectMovement(box, world);
        boolean setback = inWall && !inWallPreviously;
        inWallPreviously = inWall;
        if(setback) {
            this.connection.send(new VehicleMoveS2CPacket(rootVehicle));
            ci.cancel();
        }
    }

    @Unique
    private static boolean shouldRejectMovement(Box box, World world) {
        boolean isAboveTop = box.minY >= world.getTopY();
        boolean isBelowBottom = box.maxY <= world.getBottomY();
        if(!isAboveTop && ! isBelowBottom) return false;
        int intersectionCheckY = isAboveTop ? world.getTopY()-1 : world.getBottomY();
        BlockPos blockPos = BlockPos.ofFloored(box.minX + 0.001, intersectionCheckY, box.minZ + 0.001);
        BlockPos blockPos2 = BlockPos.ofFloored(box.maxX - 0.001, intersectionCheckY, box.maxZ - 0.001);
        Block mazeWallBlock = ((ServerWorldAccess) world).getMazeWallBlock();
        if (world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int blockPosX = blockPos.getX(); blockPosX <= blockPos2.getX(); ++blockPosX) {
                for (int blockPosZ = blockPos.getZ(); blockPosZ <= blockPos2.getZ(); ++blockPosZ) {
                    mutable.set(blockPosX, intersectionCheckY, blockPosZ);
                    BlockState blockState = world.getBlockState(mutable);
                    if(blockState.isOf(mazeWallBlock)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
