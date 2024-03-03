package me.andreasmelone.glowingeyes.common.packets;

import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.common.capability.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class CapabilityUpdatePacket {
    UUID playerUUID;
    Player player;
    IGlowingEyes capability;

    public CapabilityUpdatePacket(Player player, IGlowingEyes capability) {
        this.player = player;
        this.capability = capability;
    }

    private CapabilityUpdatePacket(UUID playerUUID, IGlowingEyes capability) {
        this.playerUUID = playerUUID;
        this.capability = capability;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(player.getUUID());
        buffer.writeBoolean(capability.isToggledOn());
        buffer.writeByteArray(Util.serializeMap(capability.getGlowingEyesMap()));
        System.out.printf("Encoded capability: %s\n", capability.getGlowingEyesMap());
    }

    public static CapabilityUpdatePacket decode(FriendlyByteBuf buffer) {
        UUID playerUUID = buffer.readUUID();
        boolean toggledOn = buffer.readBoolean();
        byte[] glowingEyesMap = buffer.readByteArray();
        System.out.printf("UUID: %s, ToggledOn: %s, EyesBytes: %s", playerUUID, toggledOn, new String(glowingEyesMap));

        IGlowingEyes capability = new GlowingEyesImpl();
        capability.setToggledOn(toggledOn);
        capability.setGlowingEyesMap(Util.deserializeMap(glowingEyesMap));
        System.out.printf("Decoded capability: %s\n", capability.getGlowingEyesMap());
        return new CapabilityUpdatePacket(playerUUID, capability);
    }

    public void messageConsumer(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            System.out.printf("Received capability: %s\n", capability.getGlowingEyesMap());
            if(context.getDirection().getReceptionSide().isClient()) {
                System.out.println("Received capability on client");
                Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
                System.out.println(player);
                if (player != null) {
                    System.out.println("Player is not null");
                    IGlowingEyes capability = player.getCapability(GlowingEyesCapability.INSTANCE).orElse(new GlowingEyesImpl());
                    capability.setToggledOn(this.capability.isToggledOn());
                    capability.setGlowingEyesMap(this.capability.getGlowingEyesMap());
                    System.out.printf("Set capability: %s\n", capability.getGlowingEyesMap());
                }
            } else {
                System.out.println("Received capability on server");
                List<ServerPlayer> players = context.getSender().getServer().getPlayerList().getPlayers();
                this.player = context.getSender();
                for (ServerPlayer player : players) {
                    System.out.printf("Sending capability to player %s", player.getName().getString());
                    IGlowingEyes capability = this.player.getCapability(GlowingEyesCapability.INSTANCE).orElse(new GlowingEyesImpl());
                    capability.setToggledOn(this.capability.isToggledOn());
                    capability.setGlowingEyesMap(this.capability.getGlowingEyesMap());
                    System.out.printf("Set capability: %s\n", capability.getGlowingEyesMap());
                    // send packet to client
                    PacketManager.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new CapabilityUpdatePacket(player, capability)
                    );
                    System.out.println("Sent capability to player");
                }
            }
        });
        context.setPacketHandled(true);
    }
}
