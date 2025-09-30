package me.andreasmelone.glowingeyes.mixin.client;

import me.andreasmelone.glowingeyes.client.render.IGlowingEyesRenderState;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V",
            at = @At("TAIL")
    )
    public void extractRenderStateMixin(AbstractClientPlayer player, PlayerRenderState renderState, float maybePartialTick, CallbackInfo ci) {
        IGlowingEyesRenderState glowingEyesState = (IGlowingEyesRenderState) renderState;
        glowingEyesState.glowingEyes$setGlowingEyesMap(GlowingEyesComponent.getGlowingEyesMap(player));
        glowingEyesState.glowingEyes$setToggledOn(GlowingEyesComponent.isToggledOn(player));
    }
}
