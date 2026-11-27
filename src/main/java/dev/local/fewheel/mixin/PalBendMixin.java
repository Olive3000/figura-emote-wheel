package dev.local.fewheel.mixin;

import dev.local.fewheel.PreviewTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * ExtraBone: Figura reads limb bend from the PAL animation of the player with the given UUID.
 * While a wheel preview is drawn, read it from the preview mannequin (which plays the emote).
 */
@Mixin(targets = "org.figuramc.figura.compat.pal.PalBend", remap = false)
public abstract class PalBendMixin {

    @Inject(method = "bend(Ljava/util/UUID;Ljava/lang/String;)F",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void fewheel$bend(UUID uuid, String bone, CallbackInfoReturnable<Float> cir) {
        Avatar mannequin = PreviewTracker.current;
        if (mannequin == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.getUUID().equals(uuid)) return;
        Float value = PreviewTracker.palBend(mannequin, bone);
        if (value != null) cir.setReturnValue(value);
    }
}
