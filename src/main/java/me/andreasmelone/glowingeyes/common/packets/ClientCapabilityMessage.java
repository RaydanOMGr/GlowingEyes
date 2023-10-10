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

        GlowingEyes.logger.info("Player: " + player);
        if (player != null) {
            GlowingEyes.logger.info("Player not null, player: " + player.getName());
            IGlowingEyesCapability old = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

            old.setHasGlowingEyes(capability.hasGlowingEyes());
            old.setGlowingEyesType(capability.getGlowingEyesType());
            GlowingEyes.logger.info("Has glowing eyes: " + old.hasGlowingEyes() + ", type: " + old.getGlowingEyesType());
        }
    }

    @Override
    public void handleServerSide(ClientCapabilityMessage message, EntityPlayer player) {
        EntityPlayerMP playerMP = (EntityPlayerMP)player;
        IGlowingEyesCapability capibility = message.cap;
        IGlowingEyesCapability old = playerMP.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        old.setHasGlowingEyes(capibility.hasGlowingEyes());
        old.setGlowingEyesType(capibility.getGlowingEyesType());

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
            int hasGlowingEyes = buf.readInt();
            cap.setHasGlowingEyes(hasGlowingEyes == 1);
            GlowingEyes.logger.info("Buffer integer: " + hasGlowingEyes);
            GlowingEyes.logger.info("Has glowing eyes: " + cap.hasGlowingEyes() + " in number: " + (cap.hasGlowingEyes() ? 1 : 0));

            int glowingEyesType = buf.readInt();
            cap.setGlowingEyesType(glowingEyesType);
            GlowingEyes.logger.info("Buffer integer: " + glowingEyesType);
            GlowingEyes.logger.info("Glowing eyes type: " + cap.getGlowingEyesType());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(cap.hasGlowingEyes() ? 1 : 0);
        GlowingEyes.logger.info("Has glowing eyes: " + cap.hasGlowingEyes() + " in number: " + (cap.hasGlowingEyes() ? 1 : 0));
        buf.writeInt(cap.getGlowingEyesType());
        GlowingEyes.logger.info("Glowing eyes type: " + cap.getGlowingEyesType());
    }

    public ClientCapabilityMessage(IGlowingEyesCapability cap, EntityPlayer player) {
        this.cap = cap;
        this.player = player;
    }

    public ClientCapabilityMessage() {

    }
}
