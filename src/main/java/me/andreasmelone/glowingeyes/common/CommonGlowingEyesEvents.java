package me.andreasmelone.glowingeyes.common;

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

public class CommonGlowingEyesEvents {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        IGlowingEyesCapability eyes = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        if (eyes == null) return;

        NetworkHandler.sendToClient(new ClientCapabilityMessage(eyes, player), (EntityPlayerMP) player);
        for (EntityPlayer p : player.getEntityWorld().playerEntities) {
            if (p == player) continue;
            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(player, eyes), (EntityPlayerMP) p);

            IGlowingEyesCapability otherEyes = p.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);
            if (otherEyes == null) continue;
            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(p, otherEyes), (EntityPlayerMP) player);
        }
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
        for(EntityPlayer p : player.getEntityWorld().playerEntities) {
            if(p == player) continue;
            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(player, oldEyes), (EntityPlayerMP) p);
        }
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerChangedDimensionEvent event) {
        EntityPlayer player = event.player;
        IGlowingEyesCapability eyes = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        if(eyes == null) return;

        NetworkHandler.sendToClient(new ClientCapabilityMessage(eyes, player), (EntityPlayerMP) player);
        for(EntityPlayer p : player.getEntityWorld().playerEntities) {
            if(p == player) continue;
            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(player, eyes), (EntityPlayerMP) p);
        }
    }
}
