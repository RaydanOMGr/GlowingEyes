package me.andreasmelone.glowingeyes.common.component.eyes;

import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public interface IGlowingEyesComponent {
    /**
     * Gets the glowing eyes map from a player
     * @param player the player to get the glowing eyes map from
     * @return the glowing eyes map (Point, Color)
     */
    Map<Point, Color> getGlowingEyesMap(Player player);

    /**
     * Sets the glowing eyes map (should only be using when completely overwriting the map)
     * Otherwise, get the glowing eyes map and modify it
     * @param player the player to set the glowing eyes map for
     * @param glowingEyesMap the glowing eyes map to set
     */
    void setGlowingEyesMap(Player player, Map<Point, Color> glowingEyesMap);

    /**
     * Gets whether the glowing eyes are toggled on for a player
     * @param player the player to get the toggled on status for
     * @return whether the glowing eyes are toggled on
     */
    boolean isToggledOn(Player player);

    /**
     * Sets whether the glowing eyes are toggled on for a player
     * @param player the player to set the toggled on status for
     * @param toggledOn whether the glowing eyes are toggled on
     */
    void setToggledOn(Player player, boolean toggledOn);


    /**
     * Server-side-only method to send the updated component to the client
     * @param updatedPlayer the player who has been updated (will receive the update if no second player is specified)
     */
    void sendUpdate(ServerPlayer updatedPlayer);

    /**
     * Server-side-only method to send the updated component to the client
     * @param updatedPlayer the player who has been updated
     * @param receivingPlayer another player who will receive the update
     */
    void sendUpdate(ServerPlayer updatedPlayer, ServerPlayer receivingPlayer);

    /**
     * Serializes the players GlowingEyesComponent as a {@link CompoundTag}
     * @param player The player whose data must be serialized
     * @param provider The lookup provider
     * @return The serialized object
     */
    CompoundTag serialize(Player player, HolderLookup.Provider provider);

    /**
     * Loads the GlowingEyesComponent from a {@link Tag}, usually required to be serialized using {@link IGlowingEyesComponent#serialize(Player, HolderLookup.Provider)}
     * @param player The player to whom the data must be loaded
     * @param provider The lookup provider
     * @param tag The serialized object
     */
    void load(Player player, HolderLookup.Provider provider, CompoundTag tag);
}
