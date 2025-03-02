package me.andreasmelone.glowingeyes.fabric.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class GlowingEyesClientEvents {
    public static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            GlowingEyes.SCHEDULER_CLIENT.tick();
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientPlayerDataComponent.sendRequest());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> DynamicTextureCache.clear());
    }
}
