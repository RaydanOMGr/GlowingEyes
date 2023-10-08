package me.andreasmelone.glowingeyes.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class OtherPlayerCapabilityMessage extends MessageBase<OtherPlayerCapabilityMessage> {
    private IGlowingEyesCapability eyes = new GlowingEyesCapability();
    private EntityPlayer player;
    private UUID playerUUID;

    @Override
    public void handleClientSide(OtherPlayerCapabilityMessage message, EntityPlayer player) {
        IGlowingEyesCapability capability = message.eyes;
        EntityPlayer thisPlayer = player.world.getPlayerEntityByUUID(message.playerUUID);

        GlowingEyes.logger.info("Client received packet 'OtherPlayerCapabilityMessage' from server. Player uuid: " + message.playerUUID);

        if (thisPlayer != null) {
            GlowingEyes.logger.info("Player is not null, player name: " + thisPlayer.getName());
            IGlowingEyesCapability old = thisPlayer.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

            old.setHasGlowingEyes(capability.hasGlowingEyes());
            old.setGlowingEyesType(capability.getGlowingEyesType());
            GlowingEyes.logger.info("Player has glowing eyes: " + old.hasGlowingEyes() + ", glowing eyes type: " + old.getGlowingEyesType());
        }
    }

    @Override
    public void handleServerSide(OtherPlayerCapabilityMessage message, EntityPlayer player) {
        player.sendMessage(
                new TextComponentString("§4§lYour client is trying to send an invalid packet to the server!")
        );
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.isReadable()) {
            int length = buf.readInt();
            GlowingEyes.logger.info("Reading packet 'OtherPlayerCapabilityMessage' from client. Length: " + length);
            String playerUuid = buf.readBytes(length).toString(StandardCharsets.UTF_8);
            GlowingEyes.logger.info("Player uuid: " + playerUuid);
            this.playerUUID = UUID.fromString(playerUuid);
            eyes.setHasGlowingEyes(buf.readBoolean());
            eyes.setGlowingEyesType(buf.readInt());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] uuid = player.getUniqueID().toString().getBytes(StandardCharsets.UTF_8);
        GlowingEyes.logger.info("Sending packet 'OtherPlayerCapabilityMessage' to client. Player uuid: " + player.getUniqueID());
        GlowingEyes.logger.info("in bytes: " + uuid.length);

        buf.writeInt(uuid.length);
        buf.writeBytes(uuid);
        buf.writeBoolean(eyes.hasGlowingEyes());
        buf.writeInt(eyes.getGlowingEyesType());
    }

    public OtherPlayerCapabilityMessage(EntityPlayer player, IGlowingEyesCapability eyes) {
        this.player = player;
        this.eyes = eyes;
    }

    public OtherPlayerCapabilityMessage() {
    }
}
