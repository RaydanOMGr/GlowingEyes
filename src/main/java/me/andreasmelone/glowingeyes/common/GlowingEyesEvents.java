package me.andreasmelone.glowingeyes.common;

import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
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

            GlowingEyesComponent.sendUpdate(playerTracking, trackedPlayer);
        });
    }
}
