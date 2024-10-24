package me.andreasmelone.glowingeyes.forge.common.component.eyes;

import me.andreasmelone.glowingeyes.common.component.eyes.IGlowingEyesComponent;
import me.andreasmelone.glowingeyes.forge.common.packets.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.forge.common.packets.PacketManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.network.PacketDistributor;

import java.awt.*;
import java.util.Map;

public class GlowingEyesComponentImpl implements IGlowingEyesComponent {
    protected static final Capability<IGlowingEyes> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IGlowingEyes.class);
    }

    private final IGlowingEyes localComponent = new GlowingEyesImpl();

    /**
     * Get the GlowingEyes capability from a player
     * @param player the player to get the capability from
     * @return the GlowingEyes capability
     */
    public IGlowingEyes getComponent(Player player) {
        if(player.isLocalPlayer()) return localComponent;
        return player.getCapability(INSTANCE)
                .orElseThrow(() -> new IllegalStateException("Could not get GlowingEyes capability from player"));
    }

    @Override
    public Map<Point, Color> getGlowingEyesMap(Player player) {
        return getComponent(player).getGlowingEyesMap();
    }

    @Override
    public void setGlowingEyesMap(Player player, Map<Point, Color> glowingEyesMap) {
        getComponent(player).setGlowingEyesMap(glowingEyesMap);
    }

    @Override
    public boolean isToggledOn(Player player) {
        return getComponent(player).isToggledOn();
    }

    @Override
    public void setToggledOn(Player player, boolean toggledOn) {
        getComponent(player).setToggledOn(toggledOn);
    }

    @Override
    public void sendUpdate(ServerPlayer updatedPlayer) {
        sendUpdate(updatedPlayer, updatedPlayer);
    }

    @Override
    public void sendUpdate(ServerPlayer updatedPlayer, ServerPlayer receivingPlayer) {
        PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(() -> receivingPlayer);
        PacketManager.INSTANCE.send(target, new ComponentUpdatePacket(updatedPlayer, getComponent(updatedPlayer)));
    }
}