package me.andreasmelone.glowingeyes.forge.common.packets;

import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import net.minecraft.network.FriendlyByteBuf;
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
            if (!context.getDirection().getReceptionSide().isClient()) {
                PlayerDataComponent.setHasMod(context.getSender(), true);
            }
        });
        context.setPacketHandled(true);
    }
}
