package me.andreasmelone.glowingeyes.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class GlowingEyesHeadLayer<T extends Player, S extends AvatarRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    public GlowingEyesHeadLayer(LivingEntityRenderer<T, S, M> renderer) {
        super(renderer);
    }

    @Override
    public void submit(@NotNull PoseStack pose, @NotNull SubmitNodeCollector nodeCollector, int packedLight, S state, float v, float v1) {
        IGlowingEyesRenderState glowingEyesState = (IGlowingEyesRenderState) state;
        if (glowingEyesState.glowingEyes$isToggledOn() && !state.isInvisible) {
            ResourceLocation eyeOverlayResource = DynamicTextureCache.getTexture(glowingEyesState.glowingEyes$getGlowingEyesMap());

            RenderType eyeRenderType = RenderType.eyes(eyeOverlayResource);

//            int packerOverlay = LivingEntityRenderer.getOverlayCoords(state, 0);

            nodeCollector
                    .order(1)
                    .submitModel(
                            this.getParentModel(), state, pose,
                            eyeRenderType, packedLight, OverlayTexture.NO_OVERLAY,
                            -1, null, state.outlineColor, null
                    );
        }
    }
}
