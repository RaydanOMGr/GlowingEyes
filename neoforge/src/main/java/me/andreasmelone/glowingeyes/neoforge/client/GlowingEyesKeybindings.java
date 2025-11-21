package me.andreasmelone.glowingeyes.neoforge.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class GlowingEyesKeybindings {
    private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Util.id(GlowingEyes.MOD_ID, "keys"));

    public static final KeyMapping TOGGLE_MAPPING = new KeyMapping(
            "key.glowingeyes.toggle",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY
    );
    public static final KeyMapping EYES_EDITOR_MAPPING = new KeyMapping(
            "key.glowingeyes.editor.open",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY
    );

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        LogUtils.getLogger().info("Registering keybindings");
        event.registerCategory(CATEGORY);
        event.register(TOGGLE_MAPPING);
        event.register(EYES_EDITOR_MAPPING);
    }
}
