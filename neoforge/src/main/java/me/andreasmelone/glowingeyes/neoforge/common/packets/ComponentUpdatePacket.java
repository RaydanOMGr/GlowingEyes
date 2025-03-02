package me.andreasmelone.glowingeyes.neoforge.common.packets;

import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record ComponentUpdatePacket(UUID playerUUID, IGlowingEyes capability) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("glowingeyes", "component_update");

    public static ComponentUpdatePacket read(FriendlyByteBuf buffer) {
        UUID uuid = buffer.readUUID();
        IGlowingEyes capability = new GlowingEyesImpl();
        capability.setToggledOn(buffer.readBoolean());
        capability.setGlowingEyesMap(Util.deserializeMap(buffer.readByteArray()));
        return new ComponentUpdatePacket(uuid, capability);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerUUID);
        buffer.writeBoolean(capability.isToggledOn());
        buffer.writeByteArray(Util.serializeMap(capability.getGlowingEyesMap()));
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            if (context.flow().isClientbound()) {
                Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
                if (player != null) {
                    GlowingEyesComponent.setGlowingEyesMap(player, capability.getGlowingEyesMap());
                    GlowingEyesComponent.setToggledOn(player, capability.isToggledOn());
                }
            } else {
                Player sender = context.player().get();
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
                    GlowingEyesComponent.sendUpdate((ServerPlayer) sender, player);
                }
            }
        });
    }
}
