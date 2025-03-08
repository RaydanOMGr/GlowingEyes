package me.andreasmelone.glowingeyes.fabric.common.packet;

import me.andreasmelone.forgelikepackets.PacketContext;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class HasModPacket {
    public static final ResourceLocation ID = Util.id(GlowingEyes.MOD_ID, "has_mod");

    public void encode(FriendlyByteBuf buf) {
    }

    public static HasModPacket decode(FriendlyByteBuf buf) {
        return new HasModPacket();
    }

    public void handle(PacketContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.getDirection() == PacketFlow.SERVERBOUND) {
                ServerPlayer player = ctx.getSender();
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
