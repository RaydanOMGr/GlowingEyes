package me.andreasmelone.glowingeyes.fabric.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import com.moulberry.flashback.record.Recorder;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Debug(export = true)
@Mixin(Recorder.class)
public abstract class FlashbackRecorderMixin {
    @Inject(
            method = "writeSnapshot",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 9, shift = At.Shift.AFTER),
            remap = false
    )
    public void writeSnapshot(boolean asActualSnapshot, CallbackInfo ci,
                              @Local ClientLevel level,
                              @Local ClientboundPlayerInfoUpdatePacket infoUpdatePacket,
                              @Local(ordinal = 1) List<Packet<? super ClientGamePacketListener>> gamePackets) {
        Set<UUID> packetPlayerIds = infoUpdatePacket.entries().stream()
                .map(ClientboundPlayerInfoUpdatePacket.Entry::profileId)
                .collect(Collectors.toSet());

        for (AbstractClientPlayer player : level.players()) {
            if (!packetPlayerIds.contains(player.getUUID())) continue;
            ComponentUpdatePacket customPacketPayload = new ComponentUpdatePacket(player.getUUID(), GlowingEyesComponent.isToggledOn(player), GlowingEyesComponent.getGlowingEyesMap(player));
            LogUtils.getLogger().info("Adding payload to player {}! T: {}", player.getName().getString(), customPacketPayload);
            gamePackets.add(new ClientboundCustomPayloadPacket(
                    customPacketPayload
            ));
        }
    }
}
