package me.andreasmelone.glowingeyes.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class OtherPlayerCapabilityMessage extends MessageBase<OtherPlayerCapabilityMessage> {
    private IGlowingEyesCapability glowingEyesCapability = new GlowingEyesCapability();
    private EntityPlayer player;
    private UUID playerUUID;

    @Override
    public void handleClientSide(OtherPlayerCapabilityMessage message, EntityPlayer receivingPlayer) {
        // this wasn't working at first, I almost kms because it didn't work, and then it turned out...
        // I have to run it on the main thread, I hate this game
        Minecraft.getMinecraft().addScheduledTask(() -> {
            IGlowingEyesCapability capability = message.glowingEyesCapability;
            EntityPlayer player = receivingPlayer.world.getPlayerEntityByUUID(message.playerUUID);

            if (player != null) {
                IGlowingEyesCapability old = player.getCapability(GlowingEyesProvider.CAPABILITY, null);

                old.setGlowingEyesMap(capability.getGlowingEyesMap());
            } else {
                GlowingEyes.logger.error("Failed to get player from UUID");
            }
        });
    }

    @Override
    public void handleServerSide(OtherPlayerCapabilityMessage message, EntityPlayer sendingPlayer) {
        sendingPlayer.sendMessage(
                new TextComponentString("§4§lYour client is trying to send a 'OtherPlayerCapabilityPacket' packet to the server")
        );
        sendingPlayer.sendMessage(
                new TextComponentString("§4§lThis packet can only be sent from the server to a client!")
        );
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.isReadable()) {
            byte[] uuidBytes = new byte[16];
            buf.readBytes(uuidBytes);
            playerUUID = Util.getUUIDFromBytes(uuidBytes);

            HashMap<Point, Color> glowingEyesMap = new HashMap<>();
            int length = buf.readInt();
            for(int i = 0; i < length; i += 3) {
                Point point = new Point(buf.readInt(), buf.readInt());
                Color color = new Color(buf.readInt());
                glowingEyesMap.put(point, color);
            }

            glowingEyesCapability.setGlowingEyesMap(glowingEyesMap);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBytes(Util.getBytesFromUUID(player.getUniqueID()));

        buf.writeInt(glowingEyesCapability.getGlowingEyesMap().size() * 3);
        for (Point point : glowingEyesCapability.getGlowingEyesMap().keySet()) {
            buf.writeInt(point.x);
            buf.writeInt(point.y);
            buf.writeInt(glowingEyesCapability.getGlowingEyesMap().get(point).getRGB());
        }
    }

    public OtherPlayerCapabilityMessage(EntityPlayer player, IGlowingEyesCapability glowingEyesCapability) {
        this.player = player;
        this.glowingEyesCapability = glowingEyesCapability;
    }

    public OtherPlayerCapabilityMessage() {
    }
}
