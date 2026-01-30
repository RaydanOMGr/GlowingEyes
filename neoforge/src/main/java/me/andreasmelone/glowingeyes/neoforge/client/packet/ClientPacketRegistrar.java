package me.andreasmelone.glowingeyes.neoforge.client.packet;

import me.andreasmelone.glowingeyes.client.packet.ClientPacketHandler;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.common.packet.HasModPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

import java.util.function.Consumer;

public class ClientPacketRegistrar {
    public static void registerPackets(RegisterClientPayloadHandlersEvent event) {
        registerClientHandler(event, HasModPacket.TYPE, ClientPacketHandler::handleHasModPacket);
        registerClientHandler(event, ComponentUpdatePacket.TYPE, ClientPacketHandler::handleComponentUpdatePacket);
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
    void registerClientHandler(RegisterClientPayloadHandlersEvent registrar, CustomPacketPayload.Type<T> type, Consumer<T> clientHandler) {
        registrar.register(type, (packet, ctx) -> {
            clientHandler.accept(packet);
        });
    }
}
