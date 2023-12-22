package me.andreasmelone.glowingeyes.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.awt.*;
import java.util.List;
import java.util.UUID;

public class ClientCapabilityMessage extends MessageBase<ClientCapabilityMessage> {
    public IGlowingEyesCapability cap = new GlowingEyesCapability();
    public EntityPlayer player;

    @Override
    public void handleClientSide(ClientCapabilityMessage message, EntityPlayer player) {
        IGlowingEyesCapability capability = message.cap;

        if (player != null) {
            IGlowingEyesCapability old = player.getCapability(GlowingEyesProvider.CAPABILITY, null);

            old.setGlowingEyesMap(capability.getGlowingEyesMap());
        }
    }

    @Override
    public void handleServerSide(ClientCapabilityMessage message, EntityPlayer player) {
        EntityPlayerMP playerMP = (EntityPlayerMP)player;
        IGlowingEyesCapability capability = message.cap;
        IGlowingEyesCapability old = playerMP.getCapability(GlowingEyesProvider.CAPABILITY, null);

        old.setGlowingEyesMap(capability.getGlowingEyesMap());

        // This is done to be compatible with replaymod, but it doesn't work and Idk why
        NetworkHandler.sendToClient(new ClientCapabilityMessage(capability, player), playerMP);

        List<UUID> playersTracking = GlowingEyes.proxy.getPlayersTracking().get(player);
        if(playersTracking == null) return;
        for(UUID pUUID : playersTracking) {
            EntityPlayerMP p = playerMP.getServer().getPlayerList().getPlayerByUUID(pUUID);
            GlowingEyes.logger.info("Sending packet to " + p.getName());
            GlowingEyes.logger.info("Packet data: " + capability.getGlowingEyesMap().toString());
            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(player, old), p);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.isReadable()) {
            int length = buf.readInt();
            for(int i = 0; i < length; i += 3) {
                Point point = new Point(buf.readInt(), buf.readInt());
                Color color = new Color(buf.readInt());
                cap.getGlowingEyesMap().put(point, color);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(cap.getGlowingEyesMap().size() * 3);
        for(Point point : cap.getGlowingEyesMap().keySet()) {
            buf.writeInt(point.x);
            buf.writeInt(point.y);
            buf.writeInt(cap.getGlowingEyesMap().get(point).getRGB());
        }
    }

    public ClientCapabilityMessage(IGlowingEyesCapability cap, EntityPlayer player) {
        this.cap = cap;
        this.player = player;
    }

    public ClientCapabilityMessage() {

    }
}
