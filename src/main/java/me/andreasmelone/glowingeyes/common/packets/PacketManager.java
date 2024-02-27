package me.andreasmelone.glowingeyes.common.packets;

import me.andreasmelone.glowingeyes.GlowingEyes;
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
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GlowingEyes.MOD_ID, "main_network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerAll() {
        register(CapabilityUpdatePacket.class, CapabilityUpdatePacket::encode, CapabilityUpdatePacket::decode, CapabilityUpdatePacket::messageConsumer);
    }

    static int i = 0;
    public static <MSG> void register(Class<MSG> clazz, BiConsumer<MSG, FriendlyByteBuf> encoder,
                                      Function<FriendlyByteBuf, MSG> decoder,
                                      BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
        INSTANCE.registerMessage(i++, clazz, encoder, decoder, consumer);
    }
}
