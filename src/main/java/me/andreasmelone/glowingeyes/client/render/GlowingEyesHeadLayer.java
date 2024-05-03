package me.andreasmelone.glowingeyes.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import me.andreasmelone.glowingeyes.server.component.eyes.GlowingEyesComponent;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class GlowingEyesHeadLayer<T extends Player, Q extends HumanoidModel<T>> extends RenderLayer<T, Q> {
    public GlowingEyesHeadLayer(RenderLayerParent<T, Q> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource,
                       int i, T t, float v, float v1, float v2,
                       float v3, float v4, float v5) {
        if(GlowingEyesComponent.isToggledOn(t) && !t.isInvisible()) {
            ResourceLocation eyeOverlayResource = DynamicTextureCache.getTexture(GlowingEyesComponent.getGlowingEyesMap(t));
            if(eyeOverlayResource == null) return;

            RenderType eyeRenderType = RenderType.eyes(eyeOverlayResource);
            VertexConsumer vertexBuilderEye = multiBufferSource.getBuffer(eyeRenderType);

            int packerOverlay = LivingEntityRenderer.getOverlayCoords(t, 0);

            ModelPart head = this.getParentModel().head;
            head.render(poseStack, vertexBuilderEye, i, packerOverlay);
        }
    }
}
