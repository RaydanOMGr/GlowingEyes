package me.andreasmelone.glowingeyes.neoforge.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Util;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.IGlowingEyes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public record ComponentUpdatePacket(UUID playerUUID, IGlowingEyes capability) implements CustomPacketPayload {
    public static final Type<ComponentUpdatePacket> TYPE = new Type<>(Util.id(GlowingEyes.MOD_ID, "component_update"));
    public static final StreamCodec<ByteBuf, ComponentUpdatePacket> STREAM_CODEC = Util.createStreamCodec((buffer) -> {
        UUID playerUUID = buffer.readUUID();
        boolean toggledOn = buffer.readBoolean();
        byte[] glowingEyesMap = buffer.readByteArray();

        IGlowingEyes capability = new GlowingEyesImpl();
        capability.setToggledOn(toggledOn);
        capability.setGlowingEyesMap(Util.deserializeMap(glowingEyesMap));

        return new ComponentUpdatePacket(playerUUID, capability);
    }, (buffer, packet) -> {
        buffer.writeUUID(packet.playerUUID);
        buffer.writeBoolean(packet.capability.isToggledOn());
        buffer.writeByteArray(Util.serializeMap(packet.capability.getGlowingEyesMap()));
    });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
                if (player != null) {
                    GlowingEyesComponent.setGlowingEyesMap(player, capability.getGlowingEyesMap());
                    GlowingEyesComponent.setToggledOn(player, capability.isToggledOn());
                }
            } else {
                Player sender = context.player();
                if (!sender.getUUID().equals(playerUUID)) return;

                GlowingEyesComponent.setGlowingEyesMap(sender, capability.getGlowingEyesMap());
                GlowingEyesComponent.setToggledOn(sender, capability.isToggledOn());

                if (sender.getServer() == null) return;

                List<ServerPlayer> players = sender.getServer().getPlayerList().getPlayers().stream().filter(p -> p != sender).toList();

                for (ServerPlayer player : players) {
                    GlowingEyesComponent.sendUpdate((ServerPlayer) sender, player);
                }
            }
        });
    }
}

