package me.andreasmelone.glowingeyes.fabric.common.component.eyes;

import me.andreasmelone.glowingeyes.common.component.eyes.IGlowingEyesComponent;
import me.andreasmelone.glowingeyes.fabric.common.component.ComponentHandler;
import me.andreasmelone.glowingeyes.fabric.common.packet.ComponentUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.HashMap;

public class GlowingEyesComponentImpl implements IGlowingEyesComponent {
    private final IGlowingEyes localComponent = new GlowingEyesImpl();

    public IGlowingEyes getComponent(Player player) {
        if(player.isLocalPlayer()) return localComponent;
        return ComponentHandler.GLOWING_EYES.get(player);
    }

    @Override
    public HashMap<Point, Color> getGlowingEyesMap(Player player) {
        return getComponent(player).getGlowingEyesMap();
    }

    @Override
    public void setGlowingEyesMap(Player player, HashMap<Point, Color> glowingEyesMap) {
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

    }
}
