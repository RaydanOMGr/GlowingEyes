package me.andreasmelone.glowingeyes.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.eyes.IGlowingEyes;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.player.Player;

public class GlowingEyesHeadLayer<T extends Player, Q extends HumanoidModel<T>> extends RenderLayer<T, Q> {
    public GlowingEyesHeadLayer(RenderLayerParent<T, Q> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource,
                       int i, T t, float v, float v1, float v2,
                       float v3, float v4, float v5) {
        IGlowingEyes glowingEyes;
    }
}
