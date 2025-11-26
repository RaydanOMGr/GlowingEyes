package me.andreasmelone.glowingeyes.fabric.mixin.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.render.BreastSide;
import com.wildfire.render.GenderLayer;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(GenderLayer.class)
public abstract class Wildfire_genderGenderLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>> {
    @Shadow
    protected abstract void renderSides(T entity, M model, PoseStack matrixStack, Consumer<BreastSide> renderer);

    @Shadow
    protected abstract void renderBreast(T entity, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay, BreastSide side);

    @Unique
    private boolean isGlowingEyesRender = false;

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wildfire/render/GenderLayer;renderSides(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/model/HumanoidModel;Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/function/Consumer;)V",
                    shift = At.Shift.AFTER
            )
    )
    @SuppressWarnings("unchecked")
    public void renderInject(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, @NotNull T ent, float limbAngle, float limbDistance, float partialTicks, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        int overlay = LivingEntityRenderer.getOverlayCoords(ent, 0.0F);
        this.isGlowingEyesRender = true;
        this.renderSides(
                ent,
                ((GenderLayer<T, M>) (Object) this).getParentModel(),
                matrixStack, (side) -> this.renderBreast(ent, matrixStack, vertexConsumerProvider, light, overlay, side)
        );
        this.isGlowingEyesRender = false;
    }

    @Inject(
            method = "getRenderLayer",
            at = @At("HEAD"),
            cancellable = true
    )
    public void renderGlowingBoobs(T ent, CallbackInfoReturnable<RenderType> cir) {
        if (this.isGlowingEyesRender && ent instanceof Player player && GlowingEyesComponent.isToggledOn(player)) {
            ResourceLocation eyeOverlayResource = DynamicTextureCache.getTexture(GlowingEyesComponent.getGlowingEyesMap(player));
            cir.setReturnValue(RenderType.eyes(eyeOverlayResource));
        }
    }
}
