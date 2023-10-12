package me.andreasmelone.glowingeyes.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;

import java.util.List;
import java.util.UUID;

public class ClientCapabilityMessage extends MessageBase<ClientCapabilityMessage> {
    public IGlowingEyesCapability cap = new GlowingEyesCapability();
    public EntityPlayer player;

    @Override
    public void handleClientSide(ClientCapabilityMessage message, EntityPlayer player) {
        IGlowingEyesCapability capability = message.cap;

        if (player != null) {
            IGlowingEyesCapability old = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

            old.setHasGlowingEyes(capability.hasGlowingEyes());
            old.setGlowingEyesType(capability.getGlowingEyesType());
        }
    }

    @Override
    public void handleServerSide(ClientCapabilityMessage message, EntityPlayer player) {
        EntityPlayerMP playerMP = (EntityPlayerMP)player;
        IGlowingEyesCapability capibility = message.cap;
        IGlowingEyesCapability old = playerMP.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        old.setHasGlowingEyes(capibility.hasGlowingEyes());
        old.setGlowingEyesType(capibility.getGlowingEyesType());

        // This is done to be compatible with replaymod, but it doesn't work and Idk why
        NetworkHandler.sendToClient(new ClientCapabilityMessage(capibility, player), playerMP);

        List<UUID> playersTracking = GlowingEyes.proxy.getPlayersTracking().get(player);
        if(playersTracking == null) return;
        for(UUID pUUID : playersTracking) {
            EntityPlayerMP p = playerMP.getServer().getPlayerList().getPlayerByUUID(pUUID);
            if(p == null) return;
            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(player, old), p);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.isReadable()) {
            cap.setHasGlowingEyes(buf.readBoolean());
            cap.setGlowingEyesType(buf.readInt());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(cap.hasGlowingEyes());
        buf.writeInt(cap.getGlowingEyesType());
    }

    public ClientCapabilityMessage(IGlowingEyesCapability cap, EntityPlayer player) {
        this.cap = cap;
        this.player = player;
    }

    public ClientCapabilityMessage() {

    }
}
