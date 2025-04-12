package me.andreasmelone.glowingeyes.client.component.data;

public interface IClientPlayerDataComponent {
    /**
     * Sends a packet to the server, so it knows the player has the mod installed
     * This is needed, so the server can send the player the glowing eyes data
     */
    void sendRequest();

    /**
     * Sets whether the mod is present on the server or not
     */
    void setIsModOnServer(boolean isModOnServer);

    /**
     * Gets whether the mod is present on the server or not
     * @return Whether the mod is on the server or no
     */
    boolean isModOnServer();
}
