package net.replaceitem.mazeworld.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.replaceitem.mazeworld.fakes.WorldCreationUIStateAccess;
import net.replaceitem.mazeworld.screen.CustomizeMazeLevelScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.WorldTab.class)
public class CreateWorldScreenWorldTabMixin {
    @Shadow
    @Final
    CreateWorldScreen this$0;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;setResponder(Ljava/util/function/Consumer;)V", ordinal = 0))
    private void addMazeConfigurationButton(CreateWorldScreen parent, CallbackInfo ci, @Local GridLayout.RowHelper helper) {
        helper.addChild(CycleButton.onOffBuilder(false).create(
                Component.translatable("selectWorld.mazeworld.generateMaze"),
                (button, value) -> ((WorldCreationUIStateAccess) this$0.getUiState()).getMazeworldState().setGenerateMaze(value)
        ));
        var customizeMazeButton = helper.addChild(Button.builder(
                Component.translatable("selectWorld.mazeworld.customizeMaze"),
                _ -> this.openMazeConfiguration()).build()
        );
        this$0.getUiState().addListener(state -> customizeMazeButton.active = ((WorldCreationUIStateAccess) state).getMazeworldState().getGenerateMaze());
    }

    @Unique
    private void openMazeConfiguration() {
        Minecraft.getInstance().gui.setScreen(new CustomizeMazeLevelScreen(this$0));
    }
}
