package me.andreasmelone.glowingeyes.server;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.server.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.server.component.eyes.GlowingEyesComponent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

public class GlowingEyesEvents {
    public static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if(!PlayerDataComponent.hasMod(handler.getPlayer())) return;
            GlowingEyesComponent.sendUpdate(handler.getPlayer());
        });

        EntityTrackingEvents.START_TRACKING.register((trackedEntity, playerTracking) -> {
            if(!(trackedEntity instanceof ServerPlayer trackedPlayer)) return;
            PlayerDataComponent.addTrackedBy(trackedPlayer, playerTracking);

            if(!PlayerDataComponent.hasMod(playerTracking)) return;
            if(!PlayerDataComponent.hasMod(trackedPlayer)) return;

            GlowingEyes.getServerScheduler().runLater(() -> {
                GlowingEyesComponent.sendUpdate(playerTracking, trackedPlayer);
            }, 1L);
        });

        EntityTrackingEvents.STOP_TRACKING.register((trackedEntity, playerTracking) -> {
            if(!(trackedEntity instanceof ServerPlayer trackedPlayer)) return;
            PlayerDataComponent.removeTrackedBy(trackedPlayer, playerTracking);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            GlowingEyes.getServerScheduler().tick();
        });
    }
}
