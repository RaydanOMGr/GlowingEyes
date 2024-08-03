package me.andreasmelone.glowingeyes.forge.common;

import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.forge.common.packets.PacketManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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

        if(!PlayerDataComponent.hasMod((ServerPlayer) event.getEntity())) return;
        if(!PlayerDataComponent.hasMod((ServerPlayer) target)) return;

        PlayerDataComponent.removeTrackedBy(event.getEntity(), target);
    }
}
