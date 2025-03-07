package me.andreasmelone.glowingeyes.neoforge.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public record ComponentUpdatePacket(UUID playerUUID, IGlowingEyes capability) implements CustomPacketPayload {
    public static final Type<ComponentUpdatePacket> TYPE = new Type<>(Util.id("glowingeyes", "component_update"));
    public static final StreamCodec<ByteBuf, ComponentUpdatePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ComponentUpdatePacket decode(ByteBuf buffer) {
            long msb = buffer.readLong();
            long lsb = buffer.readLong();
            UUID uuid = new UUID(msb, lsb);
            IGlowingEyes capability = new GlowingEyesImpl();
            capability.setToggledOn(buffer.readBoolean());
            int length = buffer.readInt();
            capability.setGlowingEyesMap(Util.deserializeMap(Util.toByteArray(buffer.readBytes(length))));
            return new ComponentUpdatePacket(uuid, capability);
        }

        @Override
        public void encode(ByteBuf buffer, ComponentUpdatePacket componentUpdatePacket) {
            buffer.writeLong(componentUpdatePacket.playerUUID.getMostSignificantBits());
            buffer.writeLong(componentUpdatePacket.playerUUID.getLeastSignificantBits());
            buffer.writeBoolean(componentUpdatePacket.capability.isToggledOn());
            byte[] array = Util.serializeMap(componentUpdatePacket.capability.getGlowingEyesMap());
            buffer.writeInt(array.length);
            buffer.writeBytes(array);
        }
    };

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
