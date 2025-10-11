package me.andreasmelone.glowingeyes.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class GlowingEyesHeadLayer<T extends Player, S extends PlayerRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    public GlowingEyesHeadLayer(LivingEntityRenderer<T, S, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, @NotNull S renderState, float v, float v1) {
        IGlowingEyesRenderState glowingEyesState = (IGlowingEyesRenderState) renderState;
        if (glowingEyesState.glowingEyes$isToggledOn() && !renderState.isInvisible) {
            ResourceLocation eyeOverlayResource = DynamicTextureCache.getTexture(glowingEyesState.glowingEyes$getGlowingEyesMap());

            RenderType eyeRenderType = RenderType.eyes(eyeOverlayResource);
            VertexConsumer vertexBuilderEye = multiBufferSource.getBuffer(eyeRenderType);

            int packerOverlay = LivingEntityRenderer.getOverlayCoords(renderState, 0);
            this.getParentModel().renderToBuffer(poseStack, vertexBuilderEye, packedLight, packerOverlay);
        }
    }
}
