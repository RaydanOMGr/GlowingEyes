package me.andreasmelone.glowingeyes.common.packet;

public class PacketManager {
    public static void registerHandlers() {
        ComponentUpdatePacket.registerHandlers();
        HasModPacket.registerHandlers();
    }
}
