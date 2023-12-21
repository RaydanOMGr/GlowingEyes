package me.andreasmelone.glowingeyes.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.nio.charset.StandardCharsets;
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
            EntityPlayer thisPlayer = Minecraft.getMinecraft().player.world.getPlayerEntityByUUID(message.playerUUID);

            if (thisPlayer != null) {
                IGlowingEyesCapability old = thisPlayer.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

                old.setGlowingEyesMap(capability.getGlowingEyesMap());
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
            int length = buf.readInt();
            this.playerUUID = UUID.fromString(buf.readBytes(length).toString(StandardCharsets.UTF_8));

            int lengthBytes = buf.readInt();
            byte[] bytes = new byte[lengthBytes];
            buf.readBytes(bytes);

            for(int i = 0; i < lengthBytes; i += 3) {
                int x = bytes[i];
                int y = bytes[i + 1];
                int rgb = bytes[i + 2];
                eyes.getGlowingEyesMap().put(new Point(x, y), new Color(rgb));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] uuid = player.getUniqueID().toString().getBytes(StandardCharsets.UTF_8);

        buf.writeInt(uuid.length);
        buf.writeBytes(uuid);
        buf.writeInt(eyes.getGlowingEyesMap().size() * 3);
        for(Point point : eyes.getGlowingEyesMap().keySet()) {
            buf.writeInt(point.x);
            buf.writeInt(point.y);
            buf.writeInt(eyes.getGlowingEyesMap().get(point).getRGB());
        }
    }

    public OtherPlayerCapabilityMessage(EntityPlayer player, IGlowingEyesCapability eyes) {
        this.player = player;
        this.eyes = eyes;
    }

    public OtherPlayerCapabilityMessage() {
    }
}
