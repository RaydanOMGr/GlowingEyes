package me.andreasmelone.glowingeyes.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.eyes.IGlowingEyesCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClientCapabilityMessage extends MessageBase<ClientCapabilityMessage> {
    public IGlowingEyesCapability cap = new GlowingEyesCapability();
    public EntityPlayer player;

    @Override
    public void handleClientSide(ClientCapabilityMessage message, EntityPlayer receivingPlayer) {
        IGlowingEyesCapability capability = message.cap;

        if (receivingPlayer != null) {
            IGlowingEyesCapability oldCapability = receivingPlayer.getCapability(GlowingEyesProvider.CAPABILITY, null);

            oldCapability.setGlowingEyesMap(capability.getGlowingEyesMap());
            oldCapability.setToggledOn(capability.isToggledOn());

            GlowingEyes.proxy.setPixelMap(capability.getGlowingEyesMap());
        }
    }

    @Override
    public void handleServerSide(ClientCapabilityMessage message, EntityPlayer sendingPlayer) {
        EntityPlayerMP playerMP = (EntityPlayerMP) sendingPlayer;
        IGlowingEyesCapability capability = message.cap;
        IGlowingEyesCapability oldCapability = playerMP.getCapability(GlowingEyesProvider.CAPABILITY, null);

        oldCapability.setGlowingEyesMap(capability.getGlowingEyesMap());
        oldCapability.setToggledOn(capability.isToggledOn());

        // This is done to be compatible with replaymod, but it doesn't work and Idk why
        NetworkHandler.sendToClient(new ClientCapabilityMessage(capability, sendingPlayer), playerMP);

        List<UUID> playersTracking = GlowingEyes.proxy.getPlayersTracking().get(sendingPlayer);
        if(playersTracking == null) return;
        for(UUID pUUID : playersTracking) {
            EntityPlayerMP p = (EntityPlayerMP) playerMP.world.getPlayerEntityByUUID(pUUID);
            NetworkHandler.sendToClient(new OtherPlayerCapabilityMessage(sendingPlayer, oldCapability), p);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.isReadable()) {
            byte toggledOn = buf.readByte();
            cap.setToggledOn(toggledOn == (byte)1);

            int length = buf.readInt();
            HashMap<Point, Color> glowingEyesMap = new HashMap<>();
            for(int i = 0; i < length; i += 3) {
                Point point = new Point(buf.readInt(), buf.readInt());
                Color color = new Color(buf.readInt());
                glowingEyesMap.put(point, color);
            }

            cap.setGlowingEyesMap(glowingEyesMap);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte((byte) (cap.isToggledOn() ? 1 : 0));

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
