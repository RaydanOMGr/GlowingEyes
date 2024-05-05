package me.andreasmelone.glowingeyes.server.capability.eyes;

import me.andreasmelone.glowingeyes.server.packets.CapabilityUpdatePacket;
import me.andreasmelone.glowingeyes.server.packets.PacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;

public class GlowingEyesCapability {
    private GlowingEyesCapability() {
    }

    protected static final Capability<IGlowingEyes> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IGlowingEyes.class);
    }

    private static final IGlowingEyes localCapability = new GlowingEyesImpl();

    /**
     * Get the GlowingEyes capability from a player
     * @param player the player to get the capability from
     * @return the GlowingEyes capability
     */
    public static IGlowingEyes getCapability(Player player) {
        if(player.isLocalPlayer()) return localCapability;
        return player.getCapability(INSTANCE)
                .orElseThrow(() -> new IllegalStateException("Could not get GlowingEyes capability from player"));
    }

    /**
     * Gets the glowing eyes map from a player
     * @param player the player to get the glowing eyes map from
     * @return the glowing eyes map (Point, Color)
     */
    @NotNull
    public static HashMap<Point, Color> getGlowingEyesMap(Player player) {
        return getCapability(player).getGlowingEyesMap();
    }

    /**
     * Sets the glowing eyes map (should only be using when completely overwriting the map)
     * Otherwise, get the glowing eyes map and modify it
     * @param player the player to set the glowing eyes map for
     * @param glowingEyesMap the glowing eyes map to set
     */
    public static void setGlowingEyesMap(Player player, @NotNull HashMap<Point, Color> glowingEyesMap) {
        getCapability(player).setGlowingEyesMap(glowingEyesMap);
    }

    /**
     * Gets whether the glowing eyes are toggled on for a player
     * @param player the player to get the toggled on status for
     * @return whether the glowing eyes are toggled on
     */
    public static boolean isToggledOn(Player player) {
        return getCapability(player).isToggledOn();
    }

    /**
     * Sets whether the glowing eyes are toggled on for a player
     * @param player the player to set the toggled on status for
     * @param toggledOn whether the glowing eyes are toggled on
     */
    public static void setToggledOn(Player player, boolean toggledOn) {
        getCapability(player).setToggledOn(toggledOn);
    }

    /**
     * Server-side only method to send the updated capability to the client
     * @param updatedPlayer the player who has been updated (will receive the update if no second player is specified)
     */
    public static void sendUpdate(ServerPlayer updatedPlayer) {
        CapabilityUpdatePacket packet = new CapabilityUpdatePacket(updatedPlayer, getCapability(updatedPlayer));
        PacketManager.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> updatedPlayer),
                packet
        );
    }

    /**
     * Server-side only method to send the updated capability to the client
     * @param updatedPlayer the player who has been updated
     * @param receivingPlayer another player who will receive the update
     */
    public static void sendUpdate(ServerPlayer updatedPlayer, ServerPlayer receivingPlayer) {
        CapabilityUpdatePacket packet = new CapabilityUpdatePacket(updatedPlayer, getCapability(updatedPlayer));
        PacketManager.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> receivingPlayer),
                packet
        );
    }

    /**
     * Client-side only method to send an update to the server
     * Throws an exception when ran on the wrong side, so make sure to check the side before calling this method
     */
    @OnlyIn(Dist.CLIENT)
    public static void sendUpdate() {
        PacketManager.INSTANCE.sendToServer(new CapabilityUpdatePacket(Minecraft.getInstance().player, localCapability));
    }
}