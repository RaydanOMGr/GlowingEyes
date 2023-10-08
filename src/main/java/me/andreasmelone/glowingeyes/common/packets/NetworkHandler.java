package me.andreasmelone.glowingeyes.common.packets;

import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
    private static SimpleNetworkWrapper INSTANCE;
    private static SimpleNetworkWrapper INSTANCE_2;

    public static void init() {
        int i = 0;
        int j = 0;

        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MODID);
        INSTANCE.registerMessage(ClientCapabilityMessage.class, ClientCapabilityMessage.class, i++, Side.SERVER);
        INSTANCE.registerMessage(ServerSyncMessage.class, ServerSyncMessage.class, i++, Side.SERVER);
        INSTANCE.registerMessage(OtherPlayerCapabilityMessage.class, OtherPlayerCapabilityMessage.class, i++, Side.SERVER);


        INSTANCE_2 = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MODID + "_client");
        INSTANCE_2.registerMessage(ClientCapabilityMessage.class, ClientCapabilityMessage.class, j++, Side.CLIENT);
        INSTANCE_2.registerMessage(ServerSyncMessage.class, ServerSyncMessage.class, j++, Side.CLIENT);
        INSTANCE_2.registerMessage(OtherPlayerCapabilityMessage.class, OtherPlayerCapabilityMessage.class, j++, Side.CLIENT);
    }

    public static void sendToServer(IMessage message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendToClient(IMessage message, EntityPlayerMP player) {
        INSTANCE_2.sendTo(message, player);
    }
}
