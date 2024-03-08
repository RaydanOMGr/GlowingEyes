package me.andreasmelone.glowingeyes.common.packets;

import me.andreasmelone.glowingeyes.common.capability.data.PlayerDataCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class HasModPacket {
    public void encode(FriendlyByteBuf buffer) {}

    public static HasModPacket decode(FriendlyByteBuf buffer) {
        return new HasModPacket();
    }

    public void messageConsumer(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()) {
                PlayerDataCapability.setHasMod(context.getSender(), true);
            } else {
                // lets do this just so my IDE stops being annoying
                if(context.getSender() == null) return;
                PlayerDataCapability.addPlayerWithMod(context.getSender());
                PacketManager.INSTANCE.send(
                        PacketDistributor.PLAYER.with(context::getSender),
                        new HasModPacket()
                );
            }
        });
        context.setPacketHandled(true);
    }
}
