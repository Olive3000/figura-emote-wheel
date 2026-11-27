package dev.local.fewheel.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientLevel.class, remap = false)
public abstract class ClientLevelMixin {

    // Emotecraft's wheel preview mannequin has id Integer.MAX_VALUE.
    // Figura looks entities up by id directly in several places (armor layer etc.).
    @Inject(method = "getEntity(I)Lnet/minecraft/world/entity/Entity;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void fewheel$previewEntity(int id, CallbackInfoReturnable<Entity> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (id == Integer.MAX_VALUE && mc.player != null && (Object) this == mc.level) {
            cir.setReturnValue(mc.player);
        }
    }
}
