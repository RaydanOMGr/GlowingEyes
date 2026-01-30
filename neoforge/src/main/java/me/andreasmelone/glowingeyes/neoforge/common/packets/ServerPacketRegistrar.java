package me.andreasmelone.glowingeyes.neoforge.common.packets;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.common.packet.HasModPacket;
import me.andreasmelone.glowingeyes.common.packet.ServerPacketHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.BiConsumer;

public class ServerPacketRegistrar {
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(GlowingEyes.MOD_ID).versioned("4").optional();
        registerBidirectional(registrar, HasModPacket.TYPE, HasModPacket.STREAM_CODEC, ServerPacketHandler::handleHasModPacket);
        registerBidirectional(registrar, ComponentUpdatePacket.TYPE, ComponentUpdatePacket.STREAM_CODEC, ServerPacketHandler::handleComponentUpdatePacket);
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

    private static <T extends CustomPacketPayload>
    void registerBidirectional(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> reader,
                               BiConsumer<T, ServerPlayer> serverHandler) {
        registrar.playBidirectional(type, reader, (packet, ctx) -> {
            serverHandler.accept(packet, (ServerPlayer) ctx.player());
        });
    }
}