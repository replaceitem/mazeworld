package mazeworld.mixin;

import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.world.GeneratorOptionsHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MoreOptionsDialog.class)
public interface MoreOptionsDialogAccessor {
    @Invoker
    void callApply(GeneratorOptionsHolder.RegistryAwareModifier modifier);
}
