package me.andreasmelone.glowingeyes.neoforge.common.component.eyes;

import me.andreasmelone.glowingeyes.common.component.eyes.IGlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.neoforge.common.packets.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.neoforge.common.packets.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;
import java.util.Optional;

public class GlowingEyesComponentImpl implements IGlowingEyesComponent {
    private final IGlowingEyes localComponent = new GlowingEyesImpl();
    private final DeferredHolder<AttachmentType<?>, AttachmentType<IGlowingEyes>> attachmentType;

    public GlowingEyesComponentImpl(DeferredHolder<AttachmentType<?>, AttachmentType<IGlowingEyes>> attachmentType) {
        this.attachmentType = attachmentType;
    }

    /**
     * Get the GlowingEyes attachment from a player
     * @param player the player to get the attachment from
     * @return the GlowingEyes attachment
     */
    public IGlowingEyes getComponent(Player player) {
        if(player.isLocalPlayer()) return localComponent;
        return Optional.ofNullable(player.getData(attachmentType))
                .orElseThrow(() -> new IllegalStateException("Could not get GlowingEyes data attachment from player"));
    }

    @Override
    public Map<Point, Color> getGlowingEyesMap(Player player) {
        return getComponent(player).getGlowingEyesMap();
    }

    @Override
    public void setGlowingEyesMap(Player player, Map<Point, Color> glowingEyesMap) {
        glowingEyesMap.replaceAll((point, color) ->
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 200)
        );
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
        PacketHandler.send(receivingPlayer, new ComponentUpdatePacket(updatedPlayer.getUUID(), getComponent(updatedPlayer)));
    }
}