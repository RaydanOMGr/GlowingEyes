package me.andreasmelone.glowingeyes.fabric.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import com.moulberry.flashback.record.Recorder;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.fabric.client.compat.flashback.ActionGlowingEyesUpdate;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Recorder.class)
public abstract class FlashbackRecorderMixin {
    @Inject(
            method = "writeSnapshot",
            at = @At(value = "NEW", target = "()Ljava/util/ArrayList;", ordinal = 5)
    )
    public void writeSnapshot(boolean asActualSnapshot, CallbackInfo ci,
                              @Local LivingEntity entity,
                              @Local(ordinal = 1) List<Packet<? super ClientGamePacketListener>> gamePackets) {
        if(entity instanceof Player player) {
            ComponentUpdatePacket customPacketPayload = new ComponentUpdatePacket(player.getUUID(), GlowingEyesComponent.isToggledOn(player), GlowingEyesComponent.getGlowingEyesMap(player));
            LogUtils.getLogger().debug("Adding payload to player {}! T: {}", player.getName().getString(), customPacketPayload);
            ActionGlowingEyesUpdate.submitUpdatePacket(customPacketPayload);
        }
    }
}
