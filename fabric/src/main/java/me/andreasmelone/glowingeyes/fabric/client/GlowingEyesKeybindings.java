package me.andreasmelone.glowingeyes.fabric.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

public class GlowingEyesKeybindings {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Util.id(GlowingEyes.MOD_ID, "keys"));

    public static final KeyMapping TOGGLE_MAPPING = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.glowingeyes.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY
    ));
    public static final KeyMapping EYES_EDITOR_MAPPING = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.glowingeyes.editor.open",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY
    ));

    public static void register(ClientModContext mod) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Player player = client.player;
            if(player == null) return;

            while(GlowingEyesKeybindings.TOGGLE_MAPPING.consumeClick()) {
                GlowingEyesComponent.setToggledOn(player, !GlowingEyesComponent.isToggledOn(player));
                ClientGlowingEyesComponent.sendUpdate();
            }
            while(GlowingEyesKeybindings.EYES_EDITOR_MAPPING.consumeClick()) {
                if(client.screen != null) return;
                client.setScreen(new EyesEditorScreen(mod));
            }
        });
    }
}
