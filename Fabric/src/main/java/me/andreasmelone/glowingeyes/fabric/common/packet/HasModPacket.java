package me.andreasmelone.glowingeyes.fabric.common.packet;

import me.andreasmelone.forgelikepackets.PacketContext;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class HasModPacket {
    public static final ResourceLocation ID = new ResourceLocation(GlowingEyes.MOD_ID, "has_mod");

    public void encode(FriendlyByteBuf buf) {
    }

    public static HasModPacket decode(FriendlyByteBuf buf) {
        return new HasModPacket();
    }

    public void handle(PacketContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.getDirection() != PacketFlow.CLIENTBOUND) {
                PlayerDataComponent.setHasMod(ctx.getSender(), true);
            }
        });
    }
}
