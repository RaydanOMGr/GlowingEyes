package me.andreasmelone.glowingeyes.neoforge.common.packets;

import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(GlowingEyes.MOD_ID);
        registrar.playToServer(HasModPacket.TYPE, HasModPacket.STREAM_CODEC, HasModPacket::handle);
        registrar.playBidirectional(ComponentUpdatePacket.TYPE, ComponentUpdatePacket.STREAM_CODEC, ComponentUpdatePacket::handle);
    }

    /**
     * Sends a packet to a certain receiver/target.
     * This method is server-only!!!
     * @param target The target to receive the packet
     * @param packet The packet to send
     */
    public static void send(ServerPlayer target, CustomPacketPayload packet) {
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
}