package me.andreasmelone.glowingeyes.mixin.client;

import me.andreasmelone.glowingeyes.client.render.IGlowingEyesRenderState;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL")
    )
    public void extractRenderStateMixin(AvatarlikeEntity avatar, AvatarRenderState renderState, float maybePartialTick, CallbackInfo ci) {
        if(!(avatar instanceof Player player)) return;
        IGlowingEyesRenderState glowingEyesState = (IGlowingEyesRenderState) renderState;
        glowingEyesState.glowingEyes$setGlowingEyesMap(GlowingEyesComponent.getGlowingEyesMap(player));
        glowingEyesState.glowingEyes$setToggledOn(GlowingEyesComponent.isToggledOn(player));
    }
}
