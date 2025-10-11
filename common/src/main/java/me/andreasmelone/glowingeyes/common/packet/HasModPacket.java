package me.andreasmelone.glowingeyes.common.packet;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record HasModPacket() implements CustomPacketPayload {
    public static final Type<HasModPacket> TYPE = new Type<>(Util.id(GlowingEyes.MOD_ID, "has_mod"));
    public static final StreamCodec<ByteBuf, HasModPacket> STREAM_CODEC = Util.emptyStreamCodec(HasModPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
