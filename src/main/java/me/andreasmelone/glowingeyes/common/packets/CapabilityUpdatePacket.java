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
        System.out.println("[ENC_CAP] Encoded capability update packet");
        System.out.printf("[ENC_CAP] GlowingEyesMap Length: %d\n", capability.getGlowingEyesMap().size());
        System.out.printf("[ENC_CAP] ToggledOn: %b\n", capability.isToggledOn());
    }

    public static CapabilityUpdatePacket decode(FriendlyByteBuf buffer) {
        UUID playerUUID = buffer.readUUID();
        boolean toggledOn = buffer.readBoolean();
        byte[] glowingEyesMap = buffer.readByteArray();

        IGlowingEyes capability = new GlowingEyesImpl();
        capability.setToggledOn(toggledOn);
        capability.setGlowingEyesMap(Util.deserializeMap(glowingEyesMap));
        System.out.println("[DEC_CAP] Decoded capability update packet");
        System.out.printf("[DEC_CAP] GlowingEyesMap Length: %d\n", capability.getGlowingEyesMap().size());
        System.out.printf("[DEC_CAP] ToggledOn: %b\n", capability.isToggledOn());
        return new CapabilityUpdatePacket(playerUUID, capability);
    }

    public void messageConsumer(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()) {
                Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
                if (player != null) {
                    GlowingEyesCapability.setGlowingEyesMap(player, capability.getGlowingEyesMap());
                    GlowingEyesCapability.setToggledOn(player, capability.isToggledOn());
                }
            } else {

                // check whether the sender is the player who has been updated
                if (!context.getSender().getUUID().equals(playerUUID)) return;
                try {
                    GlowingEyesCapability.setGlowingEyesMap(context.getSender(), capability.getGlowingEyesMap());
                    GlowingEyesCapability.setToggledOn(context.getSender(), capability.isToggledOn());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(context.getSender().getServer() == null) {
                    return;
                }

                List<ServerPlayer> players = context.getSender().getServer().getPlayerList()
                        .getPlayers()
                        .stream()
                        .filter(p -> p != context.getSender())
                        .toList();
                this.player = context.getSender();

                for (ServerPlayer player : players) {
                    GlowingEyesCapability.sendUpdate((ServerPlayer) this.player, player);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
