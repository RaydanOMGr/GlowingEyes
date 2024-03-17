package me.andreasmelone.glowingeyes.common.packet;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.common.component.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class CapabilityUpdatePacket {
    public static final ResourceLocation ID = new ResourceLocation(GlowingEyes.MOD_ID, "capability_update");

    private UUID playerUUID;
    private IGlowingEyes glowingEyes;

    public CapabilityUpdatePacket() {}

    public CapabilityUpdatePacket(UUID playerUUID, IGlowingEyes glowingEyes) {
        this.playerUUID = playerUUID;
        this.glowingEyes = glowingEyes;
    }

    public CapabilityUpdatePacket(Player player, IGlowingEyes glowingEyes) {
        this(player.getUUID(), glowingEyes);
    }

    public void deserialize(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
        glowingEyes = new GlowingEyesImpl();
        glowingEyes.setToggledOn(buf.readBoolean());
        glowingEyes.setGlowingEyesMap(Util.deserializeMap(buf.readByteArray()));
        System.out.println("Deserialized the packet");
        System.out.println("The player UUID is " + playerUUID);
        System.out.println("The glowing eyes map is " + glowingEyes.getGlowingEyesMap());
        System.out.println("The toggled on status is " + glowingEyes.isToggledOn());
    }

    public void sendToServer() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(this.playerUUID);
        buf.writeBoolean(glowingEyes.isToggledOn());
        buf.writeByteArray(Util.serializeMap(glowingEyes.getGlowingEyesMap()));
        System.out.println("Sending packet to server");
        ClientPlayNetworking.send(ID, buf);
    }

    public void sendToClient(ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(this.playerUUID);
        buf.writeBoolean(glowingEyes.isToggledOn());
        buf.writeByteArray(Util.serializeMap(glowingEyes.getGlowingEyesMap()));
        System.out.println("Sending packet to client " + player.getName().getString());
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void registerHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
            CapabilityUpdatePacket packet = new CapabilityUpdatePacket();
            packet.deserialize(buf);
            server.execute(() -> {
                Player target = server.getPlayerList().getPlayer(packet.playerUUID);
                if (target == null) return;
                GlowingEyesComponent.setGlowingEyesMap(target, packet.glowingEyes.getGlowingEyesMap());
                GlowingEyesComponent.setToggledOn(target, packet.glowingEyes.isToggledOn());

                for (ServerPlayer serverPlayer : PlayerLookup.tracking(target)) {
                    if (serverPlayer == target) return;
                    CapabilityUpdatePacket newPacket = new CapabilityUpdatePacket(target, packet.glowingEyes);
                    newPacket.sendToClient(serverPlayer);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
            CapabilityUpdatePacket packet = new CapabilityUpdatePacket();
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
