package dev.local.fewheel.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.figuramc.figura.avatar.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Emotecraft draws the wheel previews with an UnsafeMannequin living in a fake level.
 * Its getId() is hard-coded to Integer.MAX_VALUE (checked in the bytecode of build 179),
 * and Figura looks avatars up with Minecraft.level.getEntity(state.id), which finds nothing
 * for it. We tell Figura that this render state belongs to the local player.
 */
@Mixin(targets = "org.figuramc.figura.avatar.AvatarManager", remap = false)
public abstract class AvatarManagerMixin {

    private static final int EMOTECRAFT_PREVIEW_ID = Integer.MAX_VALUE;

    private static boolean fewheel$isPreview(EntityRenderState state) {
        return state instanceof AvatarRenderState s
                && s.id == EMOTECRAFT_PREVIEW_ID
                && Minecraft.getInstance().player != null;
    }

    @Inject(method = "getAvatarUnfiltered(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lorg/figuramc/figura/avatar/Avatar;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void fewheel$avatar(EntityRenderState state, CallbackInfoReturnable<Avatar> cir) {
        if (!fewheel$isPreview(state)) return;
        Avatar avatar = org.figuramc.figura.avatar.AvatarManager.getAvatarForPlayer(Minecraft.getInstance().player.getUUID());
        if (avatar != null) cir.setReturnValue(avatar);
    }

    @Inject(method = "getEntity(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/world/entity/Entity;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void fewheel$entity(EntityRenderState state, CallbackInfoReturnable<Entity> cir) {
        if (fewheel$isPreview(state)) cir.setReturnValue(Minecraft.getInstance().player);
    }
}
