package me.andreasmelone.glowingeyes.neoforge.common.packets;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record HasModPacket() implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(GlowingEyes.MOD_ID, "has_mod");

    public static HasModPacket read(FriendlyByteBuf buf) {
        return new HasModPacket();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!ctx.flow().isClientbound()) {
                PlayerDataComponent.setHasMod(ctx.player().get(), true);
                GlowingEyesComponent.sendUpdate((ServerPlayer) ctx.player().get());
            }
        });
    }
}
