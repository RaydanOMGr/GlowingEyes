package me.andreasmelone.glowingeyes.common.component.eyes;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class GlowingEyesComponent {
    public static final ResourceLocation IDENTIFIER = Util.id(GlowingEyes.MOD_ID, "glowingeyes");
    private static IGlowingEyesComponent instance;

    /**
     * Gets the glowing eyes map from a player
     * @param player the player to get the glowing eyes map from
     * @return the glowing eyes map (Point, Color)
     */
    public static Map<Point, Color> getGlowingEyesMap(Player player) {
        return instance.getGlowingEyesMap(player);
    }

    /**
     * Sets the glowing eyes map (should only be using when completely overwriting the map)
     * Otherwise, get the glowing eyes map and modify it
     * @param player the player to set the glowing eyes map for
     * @param glowingEyesMap the glowing eyes map to set
     */
    public static void setGlowingEyesMap(Player player, Map<Point, Color> glowingEyesMap) {
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

    /**
     * Serializes the players GlowingEyesComponent as a {@link CompoundTag}
     * @param player The player whose data must be serialized
     * @param provider The lookup provider
     * @return The serialized object
     */
    public static CompoundTag serialize(Player player, HolderLookup.Provider provider) {
        return instance.serialize(player, provider);
    }

    /**
     * Loads the GlowingEyesComponent from a {@link Tag}, usually required to be serialized using {@link IGlowingEyesComponent#serialize(Player, HolderLookup.Provider)}
     * @param player The player to whom the data must be loaded
     * @param provider The lookup provider
     * @param tag The serialized object
     */
    public static void load(Player player, HolderLookup.Provider provider, CompoundTag tag) {
        instance.load(player, provider, tag);
    }

    public static synchronized void setImplementation(IGlowingEyesComponent implementation) {
        if(GlowingEyesComponent.instance != null) {
            throw new IllegalStateException("GlowingEyesComponent implementation is already set");
        }
        instance = implementation;
    }

    public static IGlowingEyesComponent getImplementation() {
        return instance;
    }
}
