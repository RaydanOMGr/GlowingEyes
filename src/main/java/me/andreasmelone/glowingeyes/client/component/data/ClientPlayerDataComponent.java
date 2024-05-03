package me.andreasmelone.glowingeyes.client.component.data;

import me.andreasmelone.glowingeyes.client.packet.C2SHasModPacket;

public class ClientPlayerDataComponent {
    /**
     * Sends a packet to the server so it knows the player has the mod installed
     * This is needed, so the server can send the player the glowing eyes data
     */
    public static void sendRequest() {
        C2SHasModPacket.sendToServer();
    }
}
