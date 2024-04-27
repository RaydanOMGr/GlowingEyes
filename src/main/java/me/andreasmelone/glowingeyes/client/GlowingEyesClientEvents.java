package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;

public class GlowingEyesClientEvents {
    public static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            GlowingEyesClient.CLIENT_SCHEDULER.tick();
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> PlayerDataComponent.sendRequest());
    }
}
