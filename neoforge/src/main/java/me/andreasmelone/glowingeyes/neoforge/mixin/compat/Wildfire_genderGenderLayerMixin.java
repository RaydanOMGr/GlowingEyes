package me.andreasmelone.glowingeyes.neoforge.mixin.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.render.GenderLayer;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenderLayer.class)
public abstract class Wildfire_genderGenderLayerMixin<ENTITY extends LivingEntity, MODEL extends HumanoidModel<ENTITY>> {
    @Shadow
    protected abstract void renderBreast(LivingEntity par1, ItemStack par2, PoseStack par3, MultiBufferSource par4, RenderType par5, int par6, int par7, float par8, boolean par9, boolean par10);

    @Inject(
            method = "renderBreastWithTransforms",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wildfire/render/GenderLayer;renderBreast(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;IIFZZ)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderBreastGlowing(LivingEntity entity, HumanoidModel<ENTITY> model,
                                    ItemStack armorStack, PoseStack matrixStack, MultiBufferSource bufferSource,
                                    RenderType breastRenderType, int light, int overlay, float alpha,
                                    boolean bounceEnabled,
                                    float physPositionX, float physPositionY, float bounceRotation,
                                    float breastSize, float breastOffsetX, float breastOffsetY,
                                    float breastOffsetZ, float zOff, float outwardAngle, boolean uniboob,
                                    boolean isChestplateOccupied, boolean breathingAnimation, boolean left,
                                    boolean hasJacketLayer, CallbackInfo ci) {
        if(entity instanceof Player player && GlowingEyesComponent.isToggledOn(player)) {
            ResourceLocation eyeOverlayResource = DynamicTextureCache.getTexture(GlowingEyesComponent.getGlowingEyesMap(player));
            RenderType eyeRenderType = RenderType.eyes(eyeOverlayResource);
            this.renderBreast(entity, armorStack, matrixStack, bufferSource, eyeRenderType, light, overlay, alpha, left, hasJacketLayer);
        }
    }
}
