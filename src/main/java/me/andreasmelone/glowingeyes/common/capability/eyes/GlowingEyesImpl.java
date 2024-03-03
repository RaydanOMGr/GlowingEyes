package me.andreasmelone.glowingeyes.common.capability.eyes;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

public class GlowingEyesImpl implements IGlowingEyes {
    private boolean toggledOn = true;
    private HashMap<Point, Color> glowingEyesMap = new HashMap<>();

    @Nonnull
    @Override
    public HashMap<Point, Color> getGlowingEyesMap() {
        return this.glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMap(@Nonnull HashMap<Point, Color> glowingEyesMap) {
        this.glowingEyesMap = glowingEyesMap;
        updateTexture();
    }

    @Override
    public boolean isToggledOn() {
        return toggledOn;
    }

    @Override
    public void setToggledOn(boolean toggledOn) {
        this.toggledOn = toggledOn;
    }

    ResourceLocation resourceLocation;
    @Override
    public ResourceLocation getGlowingEyesTexture() {
        return resourceLocation;
    }

    private void updateTexture() {
        Minecraft.getInstance().execute(() -> {
            if (resourceLocation != null) {
                Minecraft.getInstance().getTextureManager().release(resourceLocation);
            }

            BufferedImage eyeOverlayTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            for (Point point : glowingEyesMap.keySet()) {
                Color color = glowingEyesMap.get(point);
                eyeOverlayTexture.setRGB(point.x + (64 / 8), point.y + (64 / 8), color.getRGB());
            }
            DynamicTexture dynamicTexture = new DynamicTexture(Util.toNativeImage(eyeOverlayTexture));
            Minecraft.getInstance().getTextureManager().register(
                    resourceLocation = new ResourceLocation(GlowingEyes.MOD_ID + ":eyes_texture" + UUID.randomUUID()),
                    dynamicTexture
            );
        });
    }
}
