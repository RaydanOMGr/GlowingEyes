package me.andreasmelone.glowingeyes.server.packet;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.server.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.server.component.eyes.GlowingEyesComponent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class S2CHasModPacket {
    public static final ResourceLocation ID = new ResourceLocation(GlowingEyes.MOD_ID, "has_mod");

    public static void sendToClient(ServerPlayer player) {
        ServerPlayNetworking.send(player, ID, PacketByteBufs.create());
    }

    public static void registerHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(S2CHasModPacket.ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                PlayerDataComponent.setHasMod(player, true);
                S2CHasModPacket.sendToClient(player);

                GlowingEyesComponent.sendUpdate(player);
                for (Player trackedByPlayer : PlayerDataComponent.getTrackedBy(player)) {
                    ServerPlayer trackedBy = (ServerPlayer) trackedByPlayer;
                    if(PlayerDataComponent.hasMod(trackedBy)) {
                        GlowingEyesComponent.sendUpdate(trackedBy, player);
                    }
                    GlowingEyesComponent.sendUpdate(player, trackedBy);
                }
            });
        });
    }
}
