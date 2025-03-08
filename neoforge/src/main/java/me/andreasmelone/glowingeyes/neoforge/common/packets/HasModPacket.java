package me.andreasmelone.glowingeyes.neoforge.common.packets;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record HasModPacket() implements CustomPacketPayload {
    public static final Type<HasModPacket> TYPE = new Type<>(Util.id(GlowingEyes.MOD_ID, "has_mod"));
    public static final StreamCodec<ByteBuf, HasModPacket> STREAM_CODEC = Util.emptyStreamCodec(HasModPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!ctx.flow().isClientbound()) {
                ServerPlayer player = (ServerPlayer) ctx.player();
                PlayerDataComponent.setHasMod(player, true);
                GlowingEyesComponent.sendUpdate(player);

                for (Player trackedByPlayer : PlayerDataComponent.getTrackedBy(player)) {
                    ServerPlayer trackedBy = (ServerPlayer) trackedByPlayer;
                    if(PlayerDataComponent.hasMod(trackedBy)) {
                        GlowingEyesComponent.sendUpdate(trackedBy, player);
                    }
                    GlowingEyesComponent.sendUpdate(player, trackedBy);
                }
            }
        });
    }
}
