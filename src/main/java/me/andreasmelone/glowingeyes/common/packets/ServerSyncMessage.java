package me.andreasmelone.glowingeyes.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

public class ServerSyncMessage extends MessageBase<ServerSyncMessage> {
    @Override
    public void handleClientSide(ServerSyncMessage message, EntityPlayer player) {
        player.sendMessage(
                new TextComponentString("The server has Glowing Eyes installed. You can use the /eyes command to toggle your glowing eyes.")
        );
        GlowingEyes.serverHasMod = true;
    }

    @Override
    public void handleServerSide(ServerSyncMessage message, EntityPlayer player) {
        GlowingEyes.logger.info("Sending server sync message to client");
        NetworkHandler.sendToClient(new ServerSyncMessage(), (EntityPlayerMP) player);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Nothing needed here, as the packet itself is the data
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // Nothing needed here, as the packet itself is the data
    }
}
