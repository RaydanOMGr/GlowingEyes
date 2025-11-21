package me.andreasmelone.glowingeyes.common.packet;

import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ServerPacketHandler {
    public static void handleHasModPacket(HasModPacket packet, ServerPlayer sender) {
        PlayerDataComponent.setHasMod(sender, true);
        PlayerDataComponent.sendUpdate(sender);
        GlowingEyesComponent.sendUpdate(sender);

        for (Player trackedByPlayer : PlayerDataComponent.getTrackedBy(sender)) {
            ServerPlayer trackedBy = (ServerPlayer) trackedByPlayer;
            if (PlayerDataComponent.hasMod(trackedBy)) {
                GlowingEyesComponent.sendUpdate(trackedBy, sender);
            }
            GlowingEyesComponent.sendUpdate(sender, trackedBy);
        }
    }

    public static void handleComponentUpdatePacket(ComponentUpdatePacket packet, ServerPlayer sender) {
        MinecraftServer server = sender.level().getServer();
        if (server == null) return;

        Player target = server.getPlayerList().getPlayer(packet.playerUUID());
        if (target == null) return;
        GlowingEyesComponent.setGlowingEyesMap(target, packet.glowingEyesMap());
        GlowingEyesComponent.setToggledOn(target, packet.toggledOn());

        for (Player serverPlayer : PlayerDataComponent.getTrackedBy(sender)) {
            if (serverPlayer == target) return;
            GlowingEyesComponent.sendUpdate(sender, (ServerPlayer) serverPlayer);
        }
    }
}
