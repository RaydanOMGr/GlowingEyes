package me.andreasmelone.glowingeyes.client.packet;

import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.common.packet.HasModPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientPacketHandler {
    public static void handleHasModPacket(HasModPacket packet) {
        ClientPlayerDataComponent.setIsModOnServer(true);
    }

    public static void handleComponentUpdatePacket(ComponentUpdatePacket packet) {
        Player target = Minecraft.getInstance().level.getPlayerByUUID(packet.playerUUID());
        if (target == null) {
            return;
        }
        GlowingEyesComponent.setGlowingEyesMap(target, packet.glowingEyesMap());
        GlowingEyesComponent.setToggledOn(target, packet.toggledOn());
    }
}
