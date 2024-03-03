package me.andreasmelone.glowingeyes.common.capability.eyes;

import me.andreasmelone.glowingeyes.common.packets.CapabilityUpdatePacket;
import me.andreasmelone.glowingeyes.common.packets.PacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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

    public static final Capability<IGlowingEyes> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IGlowingEyes.class);
    }

    private static final IGlowingEyes localCapability = new GlowingEyesImpl();
    public static IGlowingEyes getCapability(Player player) {
        if(player.isLocalPlayer()) return localCapability;
        return player.getCapability(INSTANCE)
                .orElseThrow(() -> new IllegalStateException("Could not get GlowingEyes capability from player"));
    }

    @NotNull
    public static HashMap<Point, Color> getGlowingEyesMap(Player player) {
        return getCapability(player).getGlowingEyesMap();
    }

    public static void setGlowingEyesMap(Player player, @NotNull HashMap<Point, Color> glowingEyesMap) {
        getCapability(player).setGlowingEyesMap(glowingEyesMap);
    }

    public static boolean isToggledOn(Player player) {
        return getCapability(player).isToggledOn();
    }

    public static void setToggledOn(Player player, boolean toggledOn) {
        getCapability(player).setToggledOn(toggledOn);
    }

    public static ResourceLocation getGlowingEyesTexture(Player player) {
        return getCapability(player).getGlowingEyesTexture();
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
    public static void sendUpdate() {
        PacketManager.INSTANCE.sendToServer(new CapabilityUpdatePacket(Minecraft.getInstance().player, localCapability));
    }
}