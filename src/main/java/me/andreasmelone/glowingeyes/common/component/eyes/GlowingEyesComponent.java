package me.andreasmelone.glowingeyes.common.component.eyes;

import me.andreasmelone.glowingeyes.common.component.ComponentHandler;
import me.andreasmelone.glowingeyes.common.packet.CapabilityUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.HashMap;

public class GlowingEyesComponent {
    private GlowingEyesComponent() {
    }

    private static final IGlowingEyes localComponent = new GlowingEyesImpl();

    /**
     * Get the GlowingEyes component from a player
     * You should probably not use this and instead utilize the methods in this class
     * @param player the player to get the component from
     * @return the GlowingEyes component
     */
    public static IGlowingEyes getComponent(Player player) {
        if(player.isLocalPlayer()) return localComponent;
        return ComponentHandler.GLOWING_EYES.get(player);
    }

    /**
     * Gets the glowing eyes map from a player
     * @param player the player to get the glowing eyes map from
     * @return the glowing eyes map (Point, Color)
     */
    public static HashMap<Point, Color> getGlowingEyesMap(Player player) {
        return getComponent(player).getGlowingEyesMap();
    }

    /**
     * Sets the glowing eyes map (should only be using when completely overwriting the map)
     * Otherwise, get the glowing eyes map and modify it
     * @param player the player to set the glowing eyes map for
     * @param glowingEyesMap the glowing eyes map to set
     */
    public static void setGlowingEyesMap(Player player, HashMap<Point, Color> glowingEyesMap) {
        getComponent(player).setGlowingEyesMap(glowingEyesMap);
    }

    /**
     * Gets whether the glowing eyes are toggled on for a player
     * @param player the player to get the toggled on status for
     * @return whether the glowing eyes are toggled on
     */
    public static boolean isToggledOn(Player player) {
        return getComponent(player).isToggledOn();
    }

    /**
     * Sets whether the glowing eyes are toggled on for a player
     * @param player the player to set the toggled on status for
     * @param toggledOn whether the glowing eyes are toggled on
     */
    public static void setToggledOn(Player player, boolean toggledOn) {
        getComponent(player).setToggledOn(toggledOn);
    }

    /**
     * Gets the texture for the glowing eyes for a player
     * @param player the player to get the glowing eyes texture for
     * @return the glowing eyes texture
     */
    public static ResourceLocation getGlowingEyesTexture(Player player) {
        return getComponent(player).getGlowingEyesTexture();
    }

    /**
     * Server-side only method to send the updated component to the client
     * @param updatedPlayer the player who has been updated (will receive the update if no second player is specified)
     */
    public static void sendUpdate(ServerPlayer updatedPlayer) {
        sendUpdate(updatedPlayer, updatedPlayer);
    }

    /**
     * Server-side only method to send the updated component to the client
     * @param updatedPlayer the player who has been updated
     * @param receivingPlayer another player who will receive the update
     */
    public static void sendUpdate(ServerPlayer updatedPlayer, ServerPlayer receivingPlayer) {
        new CapabilityUpdatePacket(updatedPlayer, getComponent(updatedPlayer)).sendToClient(receivingPlayer);
    }

    /**
     * Client-side only method to send an update to the server
     * Throws an exception when ran on the wrong side, so make sure to check the side before calling this method
     */
    public static void sendUpdate() {
        Player localPlayer = Minecraft.getInstance().player;
        new CapabilityUpdatePacket(localPlayer, getComponent(localPlayer)).sendToServer();
    }
}
