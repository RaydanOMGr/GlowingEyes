package me.andreasmelone.glowingeyes.fabric.common.packet;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class HasModPacket implements CustomPacketPayload {
    public static final Type<HasModPacket> TYPE = new Type<>(Util.id(GlowingEyes.MOD_ID, "has_mod"));
    public static final StreamCodec<ByteBuf, HasModPacket> STREAM_CODEC = Util.emptyStreamCodec(HasModPacket::new);

    public void handle(PacketContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.getDirection() == PacketFlow.SERVERBOUND) {
                ServerPlayer player = ctx.getSender();
                PlayerDataComponent.setHasMod(player, true);
                PlayerDataComponent.sendUpdate(player);
                GlowingEyesComponent.sendUpdate(player);

                for (Player trackedByPlayer : PlayerDataComponent.getTrackedBy(player)) {
                    ServerPlayer trackedBy = (ServerPlayer) trackedByPlayer;
                    if(PlayerDataComponent.hasMod(trackedBy)) {
                        GlowingEyesComponent.sendUpdate(trackedBy, player);
                    }
                    GlowingEyesComponent.sendUpdate(player, trackedBy);
                }
            } else {
                ClientPlayerDataComponent.setIsModOnServer(true);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
