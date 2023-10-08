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
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CommonGlowingEyesEvents {
//    @SubscribeEvent
//    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
//        EntityPlayer player = event.player;
//        IGlowingEyesCapability eyes = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);
//
//        if (eyes == null) return;
//
//        NetworkHandler.sendToClient(new ClientCapabilityMessage(eyes, player), (EntityPlayerMP) player);
//        for (EntityPlayer p : player.getEntityWorld().playerEntities) {
//            if (p == player) continue;
//            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(player, eyes), (EntityPlayerMP) p);
//
//            IGlowingEyesCapability otherEyes = p.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);
//            if (otherEyes == null) continue;
//            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(p, otherEyes), (EntityPlayerMP) player);
//        }
//    }

    @SubscribeEvent
    // AAAAAAAAAAAAAAAAAAAAAAAAA I HATE THIS STUPID EVENT
    public void onStartTracking(PlayerEvent.StartTracking event) {
        GlowingEyes.logger.info("Start tracking, entity: " + event.getTarget().getName());
        if(!(event.getTarget() instanceof EntityPlayer)) return;
        EntityPlayer player = event.getEntityPlayer();
        GlowingEyes.logger.info("Start tracking, player: " + player.getName());
        EntityPlayer target = (EntityPlayer) event.getTarget();
        GlowingEyes.logger.info("Start tracking, target: " + target.getName());

        HashMap<EntityPlayer, List<UUID>> playersTracking = GlowingEyes.proxy.getPlayersTracking();
        if(playersTracking.containsKey(player)) {
            List<UUID> tracking = playersTracking.get(player);
            if(tracking.contains(target.getUniqueID())) return;
            tracking.add(target.getUniqueID());
        } else {
            List<UUID> tracking = new ArrayList<>();
            tracking.add(target.getUniqueID());
            playersTracking.put(player, tracking);
        }
        GlowingEyes.proxy.setPlayersTracking(playersTracking);

        IGlowingEyesCapability eyes = target.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        GlowingEyes.logger.info("Start tracking, Checking if eyes are null");
        if(eyes == null) return;
        GlowingEyes.logger.info("Start tracking, eyes are not null");
        GlowingEyes.logger.info("Start tracking, has eyes: " + eyes.hasGlowingEyes() + ", type: " + eyes.getGlowingEyesType());

        NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(target, eyes), (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public void onStopTracking(PlayerEvent.StopTracking event) {
        GlowingEyes.logger.info("Stop tracking, entity: " + event.getTarget().getName());

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

        GlowingEyes.logger.info("Stop tracking, player: " + player.getName());
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer player = event.getEntityPlayer();
        IGlowingEyesCapability eyes = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);
        IGlowingEyesCapability oldEyes = event.getOriginal().getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        if(eyes == null || oldEyes == null) return;
        eyes.setHasGlowingEyes(oldEyes.hasGlowingEyes());
        eyes.setGlowingEyesType(oldEyes.getGlowingEyesType());

        NetworkHandler.sendToClient(new ClientCapabilityMessage(eyes, player), (EntityPlayerMP) player);
//        for(EntityPlayer p : player.getEntityWorld().playerEntities) {
//            if(p == player) continue;
//            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(player, oldEyes), (EntityPlayerMP) p);
//        }
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerChangedDimensionEvent event) {
        EntityPlayer player = event.player;
        IGlowingEyesCapability eyes = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        if(eyes == null) return;

        NetworkHandler.sendToClient(new ClientCapabilityMessage(eyes, player), (EntityPlayerMP) player);
//        for(EntityPlayer p : player.getEntityWorld().playerEntities) {
//            if(p == player) continue;
//            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(player, eyes), (EntityPlayerMP) p);
//        }
    }
}
