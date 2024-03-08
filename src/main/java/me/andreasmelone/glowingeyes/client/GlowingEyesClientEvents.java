package me.andreasmelone.glowingeyes.client;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GlowingEyesClientEvents {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            GlowingEyesClient.CLIENT_SCHEDULER.tick();
        }
    }
}
