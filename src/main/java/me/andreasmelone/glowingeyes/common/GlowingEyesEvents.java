package me.andreasmelone.glowingeyes.common;

import me.andreasmelone.glowingeyes.common.capability.data.PlayerDataCapability;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.PacketManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GlowingEyesEvents {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(!PacketManager.isModPresent((ServerPlayer) event.getEntity())) return;
        
        GlowingEyesCapability.sendUpdate((ServerPlayer) event.getEntity());
    }
    
    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if(!(event.getTarget() instanceof Player target)) return;

        ServerPlayer entity = (ServerPlayer) event.getEntity();
        ServerPlayer serverTarget = (ServerPlayer) target;

        if(!PacketManager.isModPresent(entity)) return;
        if(!PacketManager.isModPresent(serverTarget)) return;

        PlayerDataCapability.addTrackingPlayer(event.getEntity(), target.getUUID());
        GlowingEyesCapability.sendUpdate(serverTarget, entity);
    }

    @SubscribeEvent
    public void onPlayerStopTracking(PlayerEvent.StopTracking event) {
        if(!(event.getTarget() instanceof Player target)) return;

        if(!PacketManager.isModPresent((ServerPlayer) event.getEntity())) return;
        if(!PacketManager.isModPresent((ServerPlayer) target)) return;

        PlayerDataCapability.removeTrackingPlayer(event.getEntity(), target.getUUID());
    }
}
