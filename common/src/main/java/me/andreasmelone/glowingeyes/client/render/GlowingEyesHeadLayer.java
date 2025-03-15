package me.andreasmelone.glowingeyes.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class GlowingEyesHeadLayer<T extends Player, S extends PlayerRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    public GlowingEyesHeadLayer(LivingEntityRenderer<T, S, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, S renderState, float v, float v1) {
        if(!(Minecraft.getInstance().level.getEntity(renderState.id) instanceof Player player)) return;
        if(GlowingEyesComponent.isToggledOn(player) && !player.isInvisible()) {
            ResourceLocation eyeOverlayResource = DynamicTextureCache.getTexture(GlowingEyesComponent.getGlowingEyesMap(player));

            RenderType eyeRenderType = RenderType.eyes(eyeOverlayResource);
            VertexConsumer vertexBuilderEye = multiBufferSource.getBuffer(eyeRenderType);

            int packerOverlay = LivingEntityRenderer.getOverlayCoords(renderState, 0);
            this.getParentModel().renderToBuffer(poseStack, vertexBuilderEye, i, packerOverlay);
        }
    }
}
