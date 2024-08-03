package me.andreasmelone.glowingeyes.forge.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class GlowingEyesKeybindings {
    public static final KeyMapping TOGGLE_MAPPING = new KeyMapping(
            "key.glowingeyes.toggle",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.glowingeyes"
    );
    public static final KeyMapping EYES_EDITOR_MAPPING = new KeyMapping(
            "key.glowingeyes.editor.open",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.glowingeyes"
    );

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        LogUtils.getLogger().info("Registering keybindings");
        event.register(TOGGLE_MAPPING);
        event.register(EYES_EDITOR_MAPPING);
    }
}
