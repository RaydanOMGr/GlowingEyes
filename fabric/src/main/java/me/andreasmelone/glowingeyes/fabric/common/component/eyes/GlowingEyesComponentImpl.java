package me.andreasmelone.glowingeyes.fabric.common.component.eyes;

import me.andreasmelone.glowingeyes.common.component.eyes.IGlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.fabric.common.component.ComponentHandler;
import me.andreasmelone.glowingeyes.fabric.common.packet.ServerPacketRegistrar;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class GlowingEyesComponentImpl implements IGlowingEyesComponent {
    private final IGlowingEyes localComponent = new GlowingEyesImpl();

    public IGlowingEyes getComponent(Player player) {
        if(player.isLocalPlayer()) return this.localComponent;
        return ComponentHandler.GLOWING_EYES.get(player);
    }

    @Override
    public Map<Point, Color> getGlowingEyesMap(Player player) {
        return this.getComponent(player).getGlowingEyesMap();
    }

    @Override
    public void setGlowingEyesMap(Player player, Map<Point, Color> glowingEyesMap) {
        glowingEyesMap.replaceAll((point, color) ->
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 200)
        );
        this.getComponent(player).setGlowingEyesMap(glowingEyesMap);
    }

    @Override
    public boolean isToggledOn(Player player) {
        return this.getComponent(player).isToggledOn();
    }

    @Override
    public void setToggledOn(Player player, boolean toggledOn) {
        this.getComponent(player).setToggledOn(toggledOn);
    }

    @Override
    public void sendUpdate(ServerPlayer updatedPlayer) {
        this.sendUpdate(updatedPlayer, updatedPlayer);
    }

    @Override
    public void sendUpdate(ServerPlayer updatedPlayer, ServerPlayer receivingPlayer) {
        ServerPacketRegistrar.sendTo(receivingPlayer, new ComponentUpdatePacket(updatedPlayer.getUUID(), this.isToggledOn(updatedPlayer), this.getGlowingEyesMap(updatedPlayer)));
    }

    @Override
    public CompoundTag serialize(Player player, HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        this.getComponent(player).writeToNbt(tag, provider);
        return tag;
    }

    @Override
    public void load(Player player, HolderLookup.Provider provider, CompoundTag tag) {
        this.getComponent(player).readFromNbt(tag, provider);
    }
}
