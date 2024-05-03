package me.andreasmelone.glowingeyes.client.packet;

public class ClientPacketManager {
    public static void registerHandlers() {
        C2SComponentUpdatePacket.registerHandlers();
        C2SHasModPacket.registerHandlers();
    }
}
