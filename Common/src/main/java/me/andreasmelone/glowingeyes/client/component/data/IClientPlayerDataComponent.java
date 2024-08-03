package me.andreasmelone.glowingeyes.client.component.data;

public interface IClientPlayerDataComponent {
    /**
     * Sends a packet to the server, so it knows the player has the mod installed
     * This is needed, so the server can send the player the glowing eyes data
     */
    void sendRequest();
}
