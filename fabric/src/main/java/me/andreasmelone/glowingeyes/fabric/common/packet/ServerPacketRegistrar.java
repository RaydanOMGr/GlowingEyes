package me.andreasmelone.glowingeyes.fabric.common.packet;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.common.packet.HasModPacket;
import me.andreasmelone.glowingeyes.common.packet.ServerPacketHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public class ServerPacketRegistrar {
    public static void registerServerPackets() {
        bidirectional(HasModPacket.TYPE, HasModPacket.STREAM_CODEC, ServerPacketHandler::handleHasModPacket);
        bidirectional(ComponentUpdatePacket.TYPE, ComponentUpdatePacket.STREAM_CODEC, ServerPacketHandler::handleComponentUpdatePacket);
    }

    /**
     * Server-sided send method that sends a packet to a player.
     *
     * @param player The packet receiver
     * @param packet The packet object
     * @param <T>    The packet type
     */
    public static <T extends CustomPacketPayload> void sendTo(ServerPlayer player, T packet) {
        ServerPlayNetworking.send(player, packet);
    }

    public static <T extends CustomPacketPayload> void bidirectional(CustomPacketPayload.Type<T> type, StreamCodec<ByteBuf, T> streamCodec, BiConsumer<T, ServerPlayer> handler) {
        PayloadTypeRegistry.playS2C().register(type, streamCodec);
        server(type, streamCodec, handler);
    }

    public static <T extends CustomPacketPayload> void server(CustomPacketPayload.Type<T> type, StreamCodec<ByteBuf, T> streamCodec, BiConsumer<T, ServerPlayer> handler) {
        PayloadTypeRegistry.playC2S().register(type, streamCodec);
        ServerPlayNetworking.registerGlobalReceiver(type, (packet, ctx) -> {
            handler.accept(packet, ctx.player());
        });
    }
}
