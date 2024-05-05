package me.andreasmelone.glowingeyes.server.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.NetworkHooks;
import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketManager {
    private static final String PROTOCOL_VERSION = "1";
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(GlowingEyes.MOD_ID, "main_network");
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            IDENTIFIER,
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerAll() {
        register(CapabilityUpdatePacket.class, CapabilityUpdatePacket::encode, CapabilityUpdatePacket::decode, CapabilityUpdatePacket::messageConsumer);
    }

    public static boolean isModPresent(Connection connection) {
        final ConnectionData connectionData = NetworkHooks.getConnectionData(connection);
        if (connectionData != null) {
            return connectionData.getChannels().containsKey(IDENTIFIER);
        }

        // If the connection data is null, then we fallback to the vanilla channel list, via #isRemotePresent
        return INSTANCE.isRemotePresent(connection);
    }

    public static boolean isModPresent(ServerPlayer player) {
        Connection connection = player.connection.connection;
        return isModPresent(connection);
    }

    private static int i = 0;
    public static <MSG> void register(Class<MSG> clazz, BiConsumer<MSG, FriendlyByteBuf> encoder,
                                      Function<FriendlyByteBuf, MSG> decoder,
                                      BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
        INSTANCE.registerMessage(i++, clazz, encoder, decoder, consumer);
    }
}
