package me.andreasmelone.glowingeyes.fabric.client.packet;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.client.compat.CompatPluginRegistry;
import me.andreasmelone.glowingeyes.client.packet.ClientPacketHandler;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.common.packet.HasModPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Consumer;

public class ClientPacketRegistrar {
    public static void registerClientPackets() {
        client(HasModPacket.TYPE, HasModPacket.STREAM_CODEC, ClientPacketHandler::handleHasModPacket);
        client(ComponentUpdatePacket.TYPE, ComponentUpdatePacket.STREAM_CODEC, ClientPacketHandler::handleComponentUpdatePacket);
    }

    /**
     * Client-sided send method that sends a packet to the server.
     *
     * @param packet The packet object
     * @param <T>    The packet type
     */
    public static <T extends CustomPacketPayload> void send(T packet) {
        ClientPlayNetworking.send(packet);
        CompatPluginRegistry.raisePacketSendToServerEvent(packet);
    }

    public static <T extends CustomPacketPayload> void client(CustomPacketPayload.Type<T> type, StreamCodec<ByteBuf, T> streamCodec, Consumer<T> handler) {
        PayloadTypeRegistry.playS2C().register(type, streamCodec);
        ClientPlayNetworking.registerGlobalReceiver(type, (packet, ctx) -> {
            handler.accept(packet);
        });
    }
}
