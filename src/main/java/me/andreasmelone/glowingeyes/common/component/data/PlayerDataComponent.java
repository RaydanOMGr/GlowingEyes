package me.andreasmelone.glowingeyes.common.component.data;

import me.andreasmelone.glowingeyes.common.component.ComponentHandler;
import me.andreasmelone.glowingeyes.common.packet.HasModPacket;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class PlayerDataComponent {
    private PlayerDataComponent() {
    }

    private static final IPlayerData localComponent = new PlayerDataImpl();

    /**
     * Get the player data component for the given player
     * @param player the player to get the component for
     * @return the player data component
     */
    public static IPlayerData getComponent(Player player) {
        if(player.isLocalPlayer()) return localComponent;
        return ComponentHandler.PLAYER_DATA.get(player);
    }

    /**
     * Gets whether the player has the mod installed
     * @param player the player to check
     * @return whether the player has the mod installed
     */
    public static boolean hasMod(Player player) {
        return getComponent(player).hasMod();
    }

    /**
     * Sets whether the player has the mod installed
     * @param player the player to set the mod installed status for
     */
    public static void setHasMod(Player player, boolean hasMod) {
        getComponent(player).setHasMod(hasMod);
    }

    /**
     * Gets the players that are tracking the given player
     * @param player the player to get the tracking players for
     */
    public static Set<Player> getTrackedBy(Player player) {
        return getComponent(player).trackedBy();
    }

    /**
     * Adds a player to the list of players tracking the given player
     * @param playerTracked the player to add to the list of players tracking
     * @param trackedBy the player tracking the other player
     */
    public static void addTrackedBy(Player playerTracked, Player trackedBy) {
        getComponent(playerTracked).addTrackedBy(trackedBy);
    }

    /**
     * Removes a player from the list of players tracking the given player
     * @param playerTracked the player to remove from the list of players tracking
     * @param trackedBy the player tracking the other player
     */
    public static void removeTrackedBy(Player playerTracked, Player trackedBy) {
        getComponent(playerTracked).removeTrackedBy(trackedBy);
    }

    /**
     * Sends a packet to the server so it knows the player has the mod installed
     * This is needed, so the server can send the player the glowing eyes data
     */
    public static void sendRequest() {
        HasModPacket.sendToServer();
    }
}
