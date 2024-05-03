package me.andreasmelone.glowingeyes.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import me.andreasmelone.glowingeyes.server.component.eyes.GlowingEyesComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
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

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Player player = client.player;
            if(player == null) return;

            while(GlowingEyesKeybindings.TOGGLE_MAPPING.consumeClick()) {
                GlowingEyesComponent.setToggledOn(player, !GlowingEyesComponent.isToggledOn(player));
            }
            while(GlowingEyesKeybindings.EYES_EDITOR_MAPPING.consumeClick()) {
                if(client.screen != null) return;
                client.setScreen(new EyesEditorScreen());
            }
        });
    }
}
