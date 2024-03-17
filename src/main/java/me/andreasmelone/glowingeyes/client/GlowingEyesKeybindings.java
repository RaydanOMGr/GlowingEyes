package me.andreasmelone.glowingeyes.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class GlowingEyesKeybindings {
    public static final KeyMapping TOGGLE_MAPPING = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.glowingeyes.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.glowingeyes"
    ));
    public static final KeyMapping EYES_EDITOR_MAPPING = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.glowingeyes.editor.open",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.glowingeyes"
    ));
}
