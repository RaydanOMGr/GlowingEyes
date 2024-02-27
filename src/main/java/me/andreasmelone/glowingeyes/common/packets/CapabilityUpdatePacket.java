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
    }

    public static CapabilityUpdatePacket decode(FriendlyByteBuf buffer) {
        UUID playerUUID = buffer.readUUID();
        boolean toggledOn = buffer.readBoolean();
        byte[] glowingEyesMap = buffer.readByteArray();

        IGlowingEyes capability = new GlowingEyesImpl();
        capability.setToggledOn(toggledOn);
        capability.setGlowingEyesMap(Util.deserializeMap(glowingEyesMap));
        return new CapabilityUpdatePacket(playerUUID, capability);
    }

    public void messageConsumer(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()) {
                Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
                if (player != null) {
                    IGlowingEyes capability = player.getCapability(GlowingEyesCapability.INSTANCE).orElse(new GlowingEyesImpl());
                    capability.setToggledOn(this.capability.isToggledOn());
                    capability.setGlowingEyesMap(this.capability.getGlowingEyesMap());
                }
            } else {
                List<ServerPlayer> players = context.getSender().getServer().getPlayerList().getPlayers();
                for (ServerPlayer player : players) {
                    IGlowingEyes capability = player.getCapability(GlowingEyesCapability.INSTANCE).orElse(new GlowingEyesImpl());
                    capability.setToggledOn(this.capability.isToggledOn());
                    capability.setGlowingEyesMap(this.capability.getGlowingEyesMap());
                    // send packet to client
                    PacketManager.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new CapabilityUpdatePacket(player, capability)
                    );
                }
            }
        });
        context.setPacketHandled(true);
    }
}
