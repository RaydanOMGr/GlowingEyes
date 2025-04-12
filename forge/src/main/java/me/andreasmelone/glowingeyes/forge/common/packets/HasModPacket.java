package me.andreasmelone.glowingeyes.forge.common.packets;

import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HasModPacket {
    public void encode(FriendlyByteBuf buf) {
    }

    public static HasModPacket decode(FriendlyByteBuf buf) {
        return new HasModPacket();
    }

    public void messageConsumer(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = context.getSender();
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
        context.setPacketHandled(true);
    }
}
