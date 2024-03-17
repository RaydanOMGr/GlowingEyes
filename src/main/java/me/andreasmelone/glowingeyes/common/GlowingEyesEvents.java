package me.andreasmelone.glowingeyes.common;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

public class GlowingEyesEvents {
    public static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            GlowingEyesComponent.sendUpdate(handler.getPlayer());
        });

        EntityTrackingEvents.START_TRACKING.register((trackedEntity, playerTracking) -> {
            if(!(trackedEntity instanceof ServerPlayer trackedPlayer)) return;

            GlowingEyes.getServerScheduler().runLater(() -> {
                GlowingEyesComponent.sendUpdate(playerTracking, trackedPlayer);
            }, 1L);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            GlowingEyes.getServerScheduler().tick();
        });
    }
}
