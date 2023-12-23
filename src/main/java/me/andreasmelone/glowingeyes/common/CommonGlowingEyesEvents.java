package me.andreasmelone.glowingeyes.common;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.ClientCapabilityMessage;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import me.andreasmelone.glowingeyes.common.packets.OtherPlayerCapabilityMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CommonGlowingEyesEvents {
    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if(!(event.getTarget() instanceof EntityPlayer)) return;
        EntityPlayer player = event.getEntityPlayer();
        EntityPlayer target = (EntityPlayer) event.getTarget();

        GlowingEyes.logger.info(player.getName() + " start tracking " + target.getName());

        HashMap<EntityPlayer, List<UUID>> playersTracking = GlowingEyes.proxy.getPlayersTracking();
        if(playersTracking.containsKey(player)) {
            List<UUID> tracking = playersTracking.get(player);
            if(!tracking.contains(target.getUniqueID()))
                tracking.add(target.getUniqueID());
        } else {
            List<UUID> tracking = new ArrayList<>();
            tracking.add(target.getUniqueID());
            playersTracking.put(player, tracking);
        }
        GlowingEyes.proxy.setPlayersTracking(playersTracking);

        IGlowingEyesCapability glowingEyesCapability = target.getCapability(GlowingEyesProvider.CAPABILITY, null);
        GlowingEyes.logger.info("Sending capability to " + player.getName());
        if(glowingEyesCapability == null) return;
        GlowingEyes.logger.info("Capability is not null");

        NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(target, glowingEyesCapability), (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public void onStopTracking(PlayerEvent.StopTracking event) {
        HashMap<EntityPlayer, List<UUID>> playersTracking = GlowingEyes.proxy.getPlayersTracking();
        if(!(event.getTarget() instanceof EntityPlayer)) return;

        EntityPlayer player = event.getEntityPlayer();
        EntityPlayer target = (EntityPlayer) event.getTarget();

        if(!playersTracking.containsKey(player)) return;
        List<UUID> tracking = playersTracking.get(player);

        if(!tracking.contains(target.getUniqueID())) return;
        tracking.remove(target.getUniqueID());
        if(tracking.size() == 0) playersTracking.remove(player);
        else playersTracking.put(player, tracking);
        GlowingEyes.proxy.setPlayersTracking(playersTracking);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer player = event.getEntityPlayer();
        IGlowingEyesCapability glowingEyesCapability =
                player.getCapability(GlowingEyesProvider.CAPABILITY, null);
        IGlowingEyesCapability oldGlowingEyesCapability =
                event.getOriginal().getCapability(GlowingEyesProvider.CAPABILITY, null);

        if(glowingEyesCapability == null || oldGlowingEyesCapability == null) return;
        glowingEyesCapability.setGlowingEyesMap(oldGlowingEyesCapability.getGlowingEyesMap());

        NetworkHandler.sendToClient(new ClientCapabilityMessage(glowingEyesCapability, player), (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerChangedDimensionEvent event) {
        EntityPlayer player = event.player;
        IGlowingEyesCapability glowingEyesCapability = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        if(glowingEyesCapability == null) return;

        NetworkHandler.sendToClient(new ClientCapabilityMessage(glowingEyesCapability, player), (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
        EntityPlayer player = event.player;
        IGlowingEyesCapability glowingEyesCapability = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        if (glowingEyesCapability == null) return;

        NetworkHandler.sendToClient(new ClientCapabilityMessage(glowingEyesCapability, player), (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        // remove the player from the tracking list
        HashMap<EntityPlayer, List<UUID>> playersTracking = GlowingEyes.proxy.getPlayersTracking();
        EntityPlayer player = event.player;
        playersTracking.remove(player);
        for(List<UUID> list : playersTracking.values()) {
            list.remove(player.getUniqueID());
        }
    }
}
