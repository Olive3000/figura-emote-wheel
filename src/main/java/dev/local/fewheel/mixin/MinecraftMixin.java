package dev.local.fewheel.mixin;

import dev.local.fewheel.PreviewTracker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftMixin {

    // A preview is only "current" while the GUI is being drawn; never leak into game ticks.
    @Inject(method = "tick()V", at = @At("HEAD"), remap = false)
    private void fewheel$clearPreview(CallbackInfo ci) {
        PreviewTracker.current = null;
    }
}
