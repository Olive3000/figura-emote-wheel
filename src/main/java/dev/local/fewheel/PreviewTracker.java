package dev.local.fewheel;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Avatar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Remembers which mannequin produced which render state in Emotecraft's wheel preview,
 * and which mannequin is being drawn right now, so ExtraBone (PAL bend values) can be
 * read from the mannequin (which plays the emote) instead of the real player.
 */
public final class PreviewTracker {
    public static final int PREVIEW_ID = Integer.MAX_VALUE;

    /** render state -> preview mannequin that it was extracted from */
    public static final Map<EntityRenderState, Avatar> MAP =
            Collections.synchronizedMap(new WeakHashMap<>());

    /** mannequin whose preview is being rendered at the moment, or null */
    public static volatile Avatar current;

    private static Method getManager;
    private static Method get3DTransform;
    private static Constructor<?> boneCtor;
    private static Field bendField;
    private static boolean failed;

    private PreviewTracker() {}

    /** Same as PalBend.bend, but for the given avatar. Returns null if PAL could not be used. */
    public static Float palBend(Avatar avatar, String bone) {
        if (failed) return null;
        try {
            if (getManager == null) {
                ClassLoader cl = PreviewTracker.class.getClassLoader();
                Class<?> access = Class.forName("com.zigythebird.playeranim.api.PlayerAnimationAccess", true, cl);
                Class<?> boneClass = Class.forName("com.zigythebird.playeranimcore.bones.PlayerAnimBone", true, cl);
                Class<?> managerClass = Class.forName("com.zigythebird.playeranim.animation.AvatarAnimManager", true, cl);
                Method gm = access.getMethod("getPlayerAnimManager", Avatar.class);
                boneCtor = boneClass.getConstructor(String.class);
                bendField = boneClass.getField("bend");
                get3DTransform = managerClass.getMethod("get3DTransform", boneClass);
                getManager = gm;
            }
            Object manager = getManager.invoke(null, avatar);
            Object boneObj = boneCtor.newInstance(bone);
            get3DTransform.invoke(manager, boneObj);
            return bendField.getFloat(boneObj);
        } catch (Throwable t) {
            failed = true;
            System.err.println("[figura-emote-wheel] ExtraBone preview support disabled: " + t);
            return null;
        }
    }
}
