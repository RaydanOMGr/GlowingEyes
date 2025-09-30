package me.andreasmelone.glowingeyes.neoforge.common.packets;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.packet.ClientPacketHandler;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.common.packet.HasModPacket;
import me.andreasmelone.glowingeyes.common.packet.ServerPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PacketHandler {
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(GlowingEyes.MOD_ID).versioned("4").optional();
        registerBidirectional(registrar, HasModPacket.TYPE, HasModPacket.STREAM_CODEC, ServerPacketHandler::handleHasModPacket, ClientPacketHandler::handleHasModPacket);
        registerBidirectional(registrar, ComponentUpdatePacket.TYPE, ComponentUpdatePacket.STREAM_CODEC, ServerPacketHandler::handleComponentUpdatePacket, ClientPacketHandler::handleComponentUpdatePacket);
    }

    /**
     * Sends a packet to a certain receiver/target.
     * This method is server-only!!!
     * @param target The target to receive the packet
     * @param packet The packet to send
     */
    public static void sendTo(ServerPlayer target, CustomPacketPayload packet) {
        target.connection.send(packet);
    }

    /**
     * Sends a packet to the server.
     * This method is client-only!!!
     * @param packet The packet to send
     */
    public static void send(CustomPacketPayload packet) {
        Minecraft.getInstance().getConnection().send(packet);
    }

    private static <T extends CustomPacketPayload>
    void registerBidirectional(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> reader,
                               BiConsumer<T, ServerPlayer> serverHandler, Consumer<T> clientHandler) {
        registrar.playBidirectional(type, reader, (packet, ctx) -> {
            if (ctx.flow().isServerbound()) {
                serverHandler.accept(packet, (ServerPlayer) ctx.player());
            } else {
                clientHandler.accept(packet);
            }
        });
    }
}