package me.andreasmelone.glowingeyes.server.packet;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.server.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.server.component.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.server.component.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.server.util.Util;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class S2CComponentUpdatePacket {
    public static final ResourceLocation ID = new ResourceLocation(GlowingEyes.MOD_ID, "capability_update");

    public UUID playerUUID;
    public IGlowingEyes glowingEyes;

    public S2CComponentUpdatePacket() {}

    public S2CComponentUpdatePacket(UUID playerUUID, IGlowingEyes glowingEyes) {
        this.playerUUID = playerUUID;
        this.glowingEyes = glowingEyes;
    }

    public S2CComponentUpdatePacket(Player player, IGlowingEyes glowingEyes) {
        this(player.getUUID(), glowingEyes);
    }

    public void deserialize(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
        glowingEyes = new GlowingEyesImpl();
        glowingEyes.setToggledOn(buf.readBoolean());
        glowingEyes.setGlowingEyesMap(Util.deserializeMap(buf.readByteArray()));
    }

    public void sendToClient(ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(this.playerUUID);
        buf.writeBoolean(glowingEyes.isToggledOn());
        buf.writeByteArray(Util.serializeMap(glowingEyes.getGlowingEyesMap()));
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void registerHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(S2CComponentUpdatePacket.ID, (server, player, handler, buf, responseSender) -> {
            S2CComponentUpdatePacket packet = new S2CComponentUpdatePacket();
            packet.deserialize(buf);
            server.execute(() -> {
                Player target = server.getPlayerList().getPlayer(packet.playerUUID);
                if (target == null) return;
                GlowingEyesComponent.setGlowingEyesMap(target, packet.glowingEyes.getGlowingEyesMap());
                GlowingEyesComponent.setToggledOn(target, packet.glowingEyes.isToggledOn());

                for (ServerPlayer serverPlayer : PlayerLookup.tracking(target)) {
                    if (serverPlayer == target) return;
                    S2CComponentUpdatePacket newPacket = new S2CComponentUpdatePacket(target, packet.glowingEyes);
                    newPacket.sendToClient(serverPlayer);
                }
            });
        });
    }
}
