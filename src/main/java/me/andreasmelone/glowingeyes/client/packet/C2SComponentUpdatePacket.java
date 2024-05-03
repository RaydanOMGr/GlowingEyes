package me.andreasmelone.glowingeyes.client.packet;

import me.andreasmelone.glowingeyes.server.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.server.component.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.server.packet.S2CComponentUpdatePacket;
import me.andreasmelone.glowingeyes.server.util.Util;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class C2SComponentUpdatePacket extends S2CComponentUpdatePacket {
    public C2SComponentUpdatePacket() {}

    public C2SComponentUpdatePacket(UUID playerUUID, IGlowingEyes glowingEyes) {
        this.playerUUID = playerUUID;
        this.glowingEyes = glowingEyes;
    }

    public C2SComponentUpdatePacket(Player player, IGlowingEyes glowingEyes) {
        this(player.getUUID(), glowingEyes);
    }

    public void sendToServer() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(this.playerUUID);
        buf.writeBoolean(glowingEyes.isToggledOn());
        buf.writeByteArray(Util.serializeMap(glowingEyes.getGlowingEyesMap()));
        ClientPlayNetworking.send(ID, buf);
    }

    public static void registerHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(S2CComponentUpdatePacket.ID, (client, handler, buf, responseSender) -> {
            S2CComponentUpdatePacket packet = new S2CComponentUpdatePacket();
            packet.deserialize(buf);
            client.execute(() -> {
                Player target = client.level.getPlayerByUUID(packet.playerUUID);
                if (target == null) {
                    return;
                }
                GlowingEyesComponent.setGlowingEyesMap(target, packet.glowingEyes.getGlowingEyesMap());
                GlowingEyesComponent.setToggledOn(target, packet.glowingEyes.isToggledOn());
            });
        });
    }
}
