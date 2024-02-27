package me.andreasmelone.glowingeyes.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.util.EyesResourceCache;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

public class GlowingEyesHeadLayer<T extends Player, Q extends HumanoidModel<T>> extends RenderLayer<T, Q> {
    public GlowingEyesHeadLayer(RenderLayerParent<T, Q> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource,
                       int i, T t, float v, float v1, float v2,
                       float v3, float v4, float v5) {
        IGlowingEyes glowingEyes = t.getCapability(GlowingEyesCapability.INSTANCE)
                .orElseThrow(() -> new IllegalStateException("Could not get GlowingEyes capability from player"));
        if(glowingEyes.isToggledOn() && !t.isInvisible()) {
            HashMap<Point, Color> pixels = glowingEyes.getGlowingEyesMap();
            ResourceLocation eyeOverlayResource = EyesResourceCache.INSTANCE.get(pixels);

            RenderType eyeRenderType = RenderType.eyes(eyeOverlayResource);
            VertexConsumer vertexBuilderEye = multiBufferSource.getBuffer(eyeRenderType);

            int packerOverlay = LivingEntityRenderer.getOverlayCoords(t, 0);

            ModelPart head = this.getParentModel().head;
            head.render(poseStack, vertexBuilderEye, i, packerOverlay);
        }
    }
}
