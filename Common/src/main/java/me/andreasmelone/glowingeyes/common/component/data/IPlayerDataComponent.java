package me.andreasmelone.glowingeyes.common.component.data;

import net.minecraft.world.entity.player.Player;

import java.util.Set;

public interface IPlayerDataComponent {
    /**
     * Gets whether the player has the mod installed
     * @param player the player to check
     * @return whether the player has the mod installed
     */
    boolean hasMod(Player player);

    /**
     * Sets whether the player has the mod installed
     * @param player the player to set the mod installed status for
     */
    void setHasMod(Player player, boolean hasMod);

    /**
     * Gets the players that are tracking the given player
     * @param player the player to get the tracking players for
     */
    Set<Player> getTrackedBy(Player player);

    /**
     * Adds a player to the list of players tracking the given player
     * @param playerTracked the player to add to the list of players tracking
     * @param trackedBy the player tracking the other player
     */
    void addTrackedBy(Player playerTracked, Player trackedBy);

    /**
     * Removes a player from the list of players tracking the given player
     * @param playerTracked the player to remove from the list of players tracking
     * @param trackedBy the player tracking the other player
     */
    void removeTrackedBy(Player playerTracked, Player trackedBy);
}
