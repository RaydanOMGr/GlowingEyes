package me.andreasmelone.glowingeyes.neoforge.common;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class GlowingEyesEvents {
    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if(!(event.getTarget() instanceof Player target)) return;

        ServerPlayer entity = (ServerPlayer) event.getEntity();
        ServerPlayer serverTarget = (ServerPlayer) target;

        if(!PlayerDataComponent.hasMod(entity)) return;
        if(!PlayerDataComponent.hasMod(serverTarget)) return;

        PlayerDataComponent.addTrackedBy(event.getEntity(), target);
        GlowingEyesComponent.sendUpdate(serverTarget, entity);
    }

    @SubscribeEvent
    public void onPlayerStopTracking(PlayerEvent.StopTracking event) {
        if(!(event.getTarget() instanceof Player target)) return;

        if(!PlayerDataComponent.hasMod(event.getEntity())) return;
        if(!PlayerDataComponent.hasMod(target)) return;

        PlayerDataComponent.removeTrackedBy(event.getEntity(), target);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            GlowingEyes.SCHEDULER_SERVER.tick();
        }
    }
}
