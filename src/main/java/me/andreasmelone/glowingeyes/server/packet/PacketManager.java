package me.andreasmelone.glowingeyes.server.packet;

public class PacketManager {
    public static void registerHandlers() {
        S2CComponentUpdatePacket.registerHandlers();
        S2CHasModPacket.registerHandlers();
    }
}
