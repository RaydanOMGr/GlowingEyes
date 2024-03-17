package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.entity.player.Player;

public class GlowingEyesClientEvents {
    public static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            GlowingEyesClient.CLIENT_SCHEDULER.tick();

            Player player = client.player;
            if(player == null) return;

            while(GlowingEyesKeybindings.TOGGLE_MAPPING.consumeClick()) {
                GlowingEyesCapability.setToggledOn(player, !GlowingEyesCapability.isToggledOn(player));
            }
            while(GlowingEyesKeybindings.EYES_EDITOR_MAPPING.consumeClick()) {
                if(client.screen != null) return;
                client.setScreen(new EyesEditorScreen());
            }
        });
    }
}
