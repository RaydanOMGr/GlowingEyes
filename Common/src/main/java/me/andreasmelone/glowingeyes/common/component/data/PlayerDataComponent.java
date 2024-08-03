package me.andreasmelone.glowingeyes.common.component.data;

import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class PlayerDataComponent {
    private static IPlayerDataComponent instance;

    private PlayerDataComponent() {
    }

    /**
     * Gets whether the player has the mod installed
     * @param player the player to check
     * @return whether the player has the mod installed
     */
    public static boolean hasMod(Player player) {
        return instance.hasMod(player);
    }

    /**
     * Sets whether the player has the mod installed
     * @param player the player to set the mod installed status for
     */
    public static void setHasMod(Player player, boolean hasMod) {
        instance.setHasMod(player, hasMod);
    }

    /**
     * Gets the players that are tracking the given player
     * @param player the player to get the tracking players for
     */
    public static Set<Player> getTrackedBy(Player player) {
        return instance.getTrackedBy(player);
    }

    /**
     * Adds a player to the list of players tracking the given player
     * @param playerTracked the player to add to the list of players tracking
     * @param trackedBy the player tracking the other player
     */
    public static void addTrackedBy(Player playerTracked, Player trackedBy) {
        instance.addTrackedBy(playerTracked, trackedBy);
    }

    /**
     * Removes a player from the list of players tracking the given player
     * @param playerTracked the player to remove from the list of players tracking
     * @param trackedBy the player tracking the other player
     */
    public static void removeTrackedBy(Player playerTracked, Player trackedBy) {
        instance.removeTrackedBy(playerTracked, trackedBy);
    }

    public static void setImplementation(IPlayerDataComponent instance) {
        if(PlayerDataComponent.instance != null) {
            throw new IllegalStateException("PlayerDataComponent implementation is already set");
        }
        PlayerDataComponent.instance = instance;
    }
}
