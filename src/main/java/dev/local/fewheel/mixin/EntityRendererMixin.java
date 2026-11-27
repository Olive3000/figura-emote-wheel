package dev.local.fewheel.mixin;

import dev.local.fewheel.PreviewTracker;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderer.class, remap = false)
public abstract class EntityRendererMixin {

    // Remember which preview mannequin a render state came from.
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V",
            at = @At("TAIL"), remap = false)
    private void fewheel$trackPreview(Entity entity, EntityRenderState state, float partialTick, CallbackInfo ci) {
        if (entity.getId() == PreviewTracker.PREVIEW_ID && entity instanceof Avatar avatar) {
            PreviewTracker.MAP.put(state, avatar);
        }
    }
}
