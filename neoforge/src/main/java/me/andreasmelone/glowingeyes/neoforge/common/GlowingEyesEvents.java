package me.andreasmelone.glowingeyes.neoforge.common;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class GlowingEyesEvents {
    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof ServerPlayer trackedPlayer)) return;
        ServerPlayer playerTracking = (ServerPlayer) event.getEntity();

        PlayerDataComponent.addTrackedBy(playerTracking, trackedPlayer);

        if (!PlayerDataComponent.hasMod(playerTracking)) return;

        GlowingEyes.SCHEDULER_SERVER.runLater(() -> {
            GlowingEyesComponent.sendUpdate(trackedPlayer, playerTracking);
        }, 1L);
    }

    @SubscribeEvent
    public void onPlayerStopTracking(PlayerEvent.StopTracking event) {
        if (!(event.getTarget() instanceof Player target)) return;

        if (!PlayerDataComponent.hasMod(event.getEntity())) return;
        if (!PlayerDataComponent.hasMod(target)) return;

        PlayerDataComponent.removeTrackedBy(event.getEntity(), target);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        GlowingEyes.SCHEDULER_SERVER.tick();
    }
}
