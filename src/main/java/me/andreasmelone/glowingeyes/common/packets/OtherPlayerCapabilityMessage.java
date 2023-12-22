package me.andreasmelone.glowingeyes.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.util.UUIDUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.util.UUID;

public class OtherPlayerCapabilityMessage extends MessageBase<OtherPlayerCapabilityMessage> {
    private IGlowingEyesCapability eyes = new GlowingEyesCapability();
    private EntityPlayer player;
    private UUID playerUUID;

    @Override
    public void handleClientSide(OtherPlayerCapabilityMessage message, EntityPlayer player) {
        // this wasn't working at first, I almost kms because it didn't work, and then it turned out...
        // I have to run it on the main thread, I hate this game
        Minecraft.getMinecraft().addScheduledTask(() -> {
            IGlowingEyesCapability capability = message.eyes;
            EntityPlayer thisPlayer = Minecraft.getMinecraft().world.getPlayerEntityByUUID(message.playerUUID);

            if (thisPlayer != null) {
                IGlowingEyesCapability old = player.getCapability(GlowingEyesProvider.CAPABILITY, null);

                old.setGlowingEyesMap(capability.getGlowingEyesMap());
                GlowingEyes.logger.info("Successfully synced GlowingEyes data for player " + thisPlayer.getName());
                GlowingEyes.logger.info("GlowingEyes data: " + capability.getGlowingEyesMap().toString());
            } else {
                GlowingEyes.logger.error("Failed to get player from UUID");
            }
        });
    }

    @Override
    public void handleServerSide(OtherPlayerCapabilityMessage message, EntityPlayer player) {
        player.sendMessage(
                new TextComponentString("§4§lYour client is trying to send a 'OtherPlayerCapabilityPacket' packet to the server")
        );
        player.sendMessage(
                new TextComponentString("§4§lThis packet can only be sent from the server to a client!")
        );
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.isReadable()) {
            byte[] uuidBytes = new byte[16];
            buf.readBytes(uuidBytes);
            playerUUID = UUIDUtil.getUUIDFromBytes(uuidBytes);

            int length = buf.readInt();
            for(int i = 0; i < length; i += 3) {
                Point point = new Point(buf.readInt(), buf.readInt());
                Color color = new Color(buf.readInt());
                eyes.getGlowingEyesMap().put(point, color);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if(buf.isWritable()) {
            buf.writeBytes(UUIDUtil.getBytesFromUUID(player.getUniqueID()));

            buf.writeInt(eyes.getGlowingEyesMap().size() * 3);
            for(Point point : eyes.getGlowingEyesMap().keySet()) {
                buf.writeInt(point.x);
                buf.writeInt(point.y);
                buf.writeInt(eyes.getGlowingEyesMap().get(point).getRGB());
            }
        }
    }

    public OtherPlayerCapabilityMessage(EntityPlayer player, IGlowingEyesCapability eyes) {
        this.player = player;
        this.eyes = eyes;
    }

    public OtherPlayerCapabilityMessage() {
    }
}
