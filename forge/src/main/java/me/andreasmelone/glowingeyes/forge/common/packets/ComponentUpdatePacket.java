package me.andreasmelone.glowingeyes.forge.common.packets;

import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Util;
import me.andreasmelone.glowingeyes.forge.common.component.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.forge.common.component.eyes.IGlowingEyes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record ComponentUpdatePacket(UUID playerUUID, IGlowingEyes capability) {
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerUUID);
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
                ServerPlayer sender = context.getSender();
                if(sender == null) return;

                // check whether the sender is the player who has been updated
                if (!sender.getUUID().equals(playerUUID)) return;
                GlowingEyesComponent.setGlowingEyesMap(sender, capability.getGlowingEyesMap());
                GlowingEyesComponent.setToggledOn(sender, capability.isToggledOn());

                if (sender.getServer() == null) return;

                List<ServerPlayer> players = sender.getServer().getPlayerList()
                        .getPlayers()
                        .stream()
                        .filter(p -> p != sender)
                        .toList();

                for (ServerPlayer player : players) {
                    GlowingEyesComponent.sendUpdate(sender, player);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
