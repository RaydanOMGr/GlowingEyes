package me.andreasmelone.glowingeyes.fabric.common.packet;

import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public class PacketHandler {
    public static void init() {
        bidirectional(HasModPacket.TYPE, HasModPacket.STREAM_CODEC, HasModPacket::handle);
        bidirectional(ComponentUpdatePacket.TYPE, ComponentUpdatePacket.STREAM_CODEC, ComponentUpdatePacket::handle);
    }

    /**
     * Client-sided send method that sends a packet to the server.
     * @param packet The packet object
     * @param <T> The packet type
     */
    public static <T extends CustomPacketPayload> void send(T packet) {
        ClientPlayNetworking.send(packet);
    }

    /**
     * Server-sided send method that sends a packet to a player.
     * @param player The packet receiver
     * @param packet The packet object
     * @param <T> The packet type
     */
    public static <T extends CustomPacketPayload> void sendTo(ServerPlayer player, T packet) {
        ServerPlayNetworking.send(player, packet);
    }

    public static <T extends CustomPacketPayload> void bidirectional(CustomPacketPayload.Type<T> type, StreamCodec<ByteBuf, T> streamCodec, BiConsumer<T, PacketContext> handler) {
        client(type, streamCodec, handler);
        server(type, streamCodec, handler);
    }

    public static <T extends CustomPacketPayload> void client(CustomPacketPayload.Type<T> type, StreamCodec<ByteBuf, T> streamCodec, BiConsumer<T, PacketContext> handler) {
        PayloadTypeRegistry.playS2C().register(type, streamCodec);
        if(isClient()) {
            ClientPlayNetworking.registerGlobalReceiver(type, (packet, ctx) -> {
                handler.accept(packet, new PacketContext(Minecraft.getInstance()));
            });
        }
    }

    public static <T extends CustomPacketPayload> void server(CustomPacketPayload.Type<T> type, StreamCodec<ByteBuf, T> streamCodec, BiConsumer<T, PacketContext> handler) {
        PayloadTypeRegistry.playC2S().register(type, streamCodec);
        ServerPlayNetworking.registerGlobalReceiver(type, (packet, ctx) -> {
            handler.accept(packet, new PacketContext(ctx.player()));
        });
    }

    private static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
