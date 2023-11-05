package me.andreasmelone.glowingeyes.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;

public class ServerSyncMessage extends MessageBase<ServerSyncMessage> {
    @Override
    public void handleClientSide(ServerSyncMessage message, EntityPlayer player) {
        // fucking annoying message, go fuck yourself
//        player.sendMessage(
//                new TextComponentString("The server has Glowing Eyes installed. You can use the /eyes command to toggle your glowing eyes.")
//        );
        GlowingEyes.serverHasMod = true;
    }

    @Override
    public void handleServerSide(ServerSyncMessage message, EntityPlayer player) {
        NetworkHandler.sendToClient(new ServerSyncMessage(), (EntityPlayerMP) player);
        IGlowingEyesCapability cap = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        if(cap == null) return;

        NetworkHandler.sendToClient(new ClientCapabilityMessage(cap, player), (EntityPlayerMP) player);
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
