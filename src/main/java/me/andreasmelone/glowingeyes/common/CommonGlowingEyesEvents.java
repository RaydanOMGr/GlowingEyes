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
    // AAAAAAAAAAAAAAAAAAAAAAAAA I HATE THIS STUPID EVENT
    public void onStartTracking(PlayerEvent.StartTracking event) {
        // Lets pretend I know what I am doing and this works, okay?
        if(!(event.getTarget() instanceof EntityPlayer)) return;
        EntityPlayer player = event.getEntityPlayer();
        EntityPlayer target = (EntityPlayer) event.getTarget();

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

        if(eyes == null) return;

        NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(target, eyes), (EntityPlayerMP) player);
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
        IGlowingEyesCapability eyes = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);
        IGlowingEyesCapability oldEyes = event.getOriginal().getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        if(eyes == null || oldEyes == null) return;
        eyes.setGlowingEyesMap(oldEyes.getGlowingEyesMap());

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
