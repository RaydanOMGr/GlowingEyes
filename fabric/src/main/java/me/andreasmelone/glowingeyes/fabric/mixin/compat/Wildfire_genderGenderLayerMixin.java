package me.andreasmelone.glowingeyes.fabric.mixin.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.render.BreastSide;
import com.wildfire.render.GenderLayer;
import me.andreasmelone.glowingeyes.client.render.IGlowingEyesRenderState;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(GenderLayer.class)
public abstract class Wildfire_genderGenderLayerMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>> {
    @Shadow
    protected abstract void renderSides(S state, M model, PoseStack matrixStack, Consumer<BreastSide> renderer);

    @Shadow
    protected abstract void renderBreast(S state, PoseStack matrixStack, SubmitNodeCollector queue, int overlay, BreastSide side);

    @Unique
    private boolean glowingEyes$isGlowingEyesRender = false;

    @Inject(
            method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wildfire/render/GenderLayer;renderSides(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;Lnet/minecraft/client/model/HumanoidModel;Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/function/Consumer;)V",
                    shift = At.Shift.AFTER
            )
    )
    @SuppressWarnings("unchecked")
    public void renderInject(PoseStack matrixStack, SubmitNodeCollector queue, int light, S state, float limbAngle, float limbDistance, CallbackInfo ci) {
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        this.glowingEyes$isGlowingEyesRender = true;
        this.renderSides(
                state,
                ((GenderLayer<S, M>) (Object) this).getParentModel(),
                matrixStack, (side) -> this.renderBreast(state, matrixStack, queue, overlay, side)
        );
        this.glowingEyes$isGlowingEyesRender = false;
    }

    @Inject(
            method = "getRenderLayer",
            at = @At("HEAD"),
            cancellable = true
    )
    public void renderGlowingBoobs(S state, CallbackInfoReturnable<RenderType> cir) {
        if (this.glowingEyes$isGlowingEyesRender && state instanceof IGlowingEyesRenderState eyesState) {
            Identifier eyeOverlayResource = DynamicTextureCache.getTexture(eyesState.glowingEyes$getGlowingEyesMap());
            cir.setReturnValue(RenderTypes.eyes(eyeOverlayResource));
        }
    }
}
