package me.andreasmelone.glowingeyes.neoforge.common.packets;

import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketHandler {
    public static void registerPackets(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(GlowingEyes.MOD_ID).optional().versioned("3");
        registrar.play(HasModPacket.ID, HasModPacket::read, handler -> handler
                .client(HasModPacket::handle)
                .server(HasModPacket::handle));
        registrar.play(ComponentUpdatePacket.ID, ComponentUpdatePacket::read, handler -> handler
                .client(ComponentUpdatePacket::handle)
                .server(ComponentUpdatePacket::handle));
    }

    /**
     * Sends a packet to a certain receiver/target.
     * This method is server-only!!!
     * @param target The target to receive the packet
     * @param packet The packet to send
     */
    public static void sendTo(PacketDistributor.PacketTarget target, CustomPacketPayload packet) {
        target.send(packet);
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