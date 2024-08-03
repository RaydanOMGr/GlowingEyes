package me.andreasmelone.glowingeyes.common.component.eyes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.HashMap;

public class GlowingEyesComponent {
    private static IGlowingEyesComponent instance;

    /**
     * Gets the glowing eyes map from a player
     * @param player the player to get the glowing eyes map from
     * @return the glowing eyes map (Point, Color)
     */
    public static HashMap<Point, Color> getGlowingEyesMap(Player player) {
        return instance.getGlowingEyesMap(player);
    }

    /**
     * Sets the glowing eyes map (should only be using when completely overwriting the map)
     * Otherwise, get the glowing eyes map and modify it
     * @param player the player to set the glowing eyes map for
     * @param glowingEyesMap the glowing eyes map to set
     */
    public static void setGlowingEyesMap(Player player, HashMap<Point, Color> glowingEyesMap) {
        instance.setGlowingEyesMap(player, glowingEyesMap);
    }

    /**
     * Gets whether the glowing eyes are toggled on for a player
     * @param player the player to get the toggled on status for
     * @return whether the glowing eyes are toggled on
     */
    public static boolean isToggledOn(Player player) {
        return instance.isToggledOn(player);
    }

    /**
     * Sets whether the glowing eyes are toggled on for a player
     * @param player the player to set the toggled on status for
     * @param toggledOn whether the glowing eyes are toggled on
     */
    public static void setToggledOn(Player player, boolean toggledOn) {
        instance.setToggledOn(player, toggledOn);
    }


    /**
     * Server-side-only method to send the updated component to the client
     * @param updatedPlayer the player who has been updated (will receive the update if no second player is specified)
     */
    public static void sendUpdate(ServerPlayer updatedPlayer) {
        instance.sendUpdate(updatedPlayer);
    }

    /**
     * Server-side-only method to send the updated component to the client
     * @param updatedPlayer the player who has been updated
     * @param receivingPlayer another player who will receive the update
     */
    public static void sendUpdate(ServerPlayer updatedPlayer, ServerPlayer receivingPlayer) {
        instance.sendUpdate(updatedPlayer, receivingPlayer);
    }

    public static void setImplementation(IGlowingEyesComponent implementation) {
        if(GlowingEyesComponent.instance != null) {
            throw new IllegalStateException("GlowingEyesComponent implementation is already set");
        }
        instance = implementation;
    }

    public static IGlowingEyesComponent getImplementation() {
        return instance;
    }
}
