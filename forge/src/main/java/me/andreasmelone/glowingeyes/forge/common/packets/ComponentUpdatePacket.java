package me.andreasmelone.glowingeyes.forge.common.packets;

import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.forge.common.component.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.forge.common.component.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ComponentUpdatePacket {
    UUID playerUUID;
    Player player;
    IGlowingEyes capability;

    public ComponentUpdatePacket(Player player, IGlowingEyes capability) {
        this.player = player;
        this.capability = capability == null ? new GlowingEyesImpl() : capability;
    }

    private ComponentUpdatePacket(UUID playerUUID, IGlowingEyes capability) {
        this.playerUUID = playerUUID;
        this.capability = capability == null ? new GlowingEyesImpl() : capability;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(player.getUUID());
        buffer.writeBoolean(capability.isToggledOn());
        buffer.writeByteArray(Util.serializeMap(capability.getGlowingEyesMap()));
    }

    public static ComponentUpdatePacket decode(FriendlyByteBuf buffer) {
        UUID playerUUID = buffer.readUUID();

        IGlowingEyes capability = new GlowingEyesImpl();
        capability.setToggledOn(buffer.readBoolean());
        capability.setGlowingEyesMap(Util.deserializeMap(buffer.readByteArray()));

        return new ComponentUpdatePacket(playerUUID, capability);
    }

    public void messageConsumer(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
                if (player != null) {
                    GlowingEyesComponent.setGlowingEyesMap(player, capability.getGlowingEyesMap());
                    GlowingEyesComponent.setToggledOn(player, capability.isToggledOn());
                }
            } else {
                if(context.getSender() == null) return;

                // check whether the sender is the player who has been updated
                if (!context.getSender().getUUID().equals(playerUUID)) return;
                GlowingEyesComponent.setGlowingEyesMap(context.getSender(), capability.getGlowingEyesMap());
                GlowingEyesComponent.setToggledOn(context.getSender(), capability.isToggledOn());

                if (context.getSender().getServer() == null) return;

                List<ServerPlayer> players = context.getSender().getServer().getPlayerList()
                        .getPlayers()
                        .stream()
                        .filter(p -> p != context.getSender())
                        .toList();
                this.player = context.getSender();

                for (ServerPlayer player : players) {
                    GlowingEyesComponent.sendUpdate((ServerPlayer) this.player, player);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
