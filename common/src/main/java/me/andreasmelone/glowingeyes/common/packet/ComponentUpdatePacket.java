package me.andreasmelone.glowingeyes.common.packet;

import io.netty.buffer.ByteBuf;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ComponentUpdatePacket(UUID playerUUID, boolean toggledOn,
                                    Map<Point, Color> glowingEyesMap) implements CustomPacketPayload {
    public static final Type<ComponentUpdatePacket> TYPE = new Type<>(Util.id(GlowingEyes.MOD_ID, "capability_update"));
    public static final StreamCodec<ByteBuf, ComponentUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ComponentUpdatePacket::playerUUID,
            ByteBufCodecs.BOOL,
            ComponentUpdatePacket::toggledOn,
            ByteBufCodecs.map(HashMap::new, Point.STREAM_CODEC, Color.STREAM_CODEC),
            ComponentUpdatePacket::glowingEyesMap,
            ComponentUpdatePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
