package me.andreasmelone.glowingeyes.common.component.eyes;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

public class GlowingEyesImpl implements IGlowingEyes {
    private boolean toggledOn = true;
    private HashMap<Point, Color> glowingEyesMap = new HashMap<>();

    @Override
    public HashMap<Point, Color> getGlowingEyesMap() {
        return this.glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMap(HashMap<Point, Color> glowingEyesMap) {
        this.glowingEyesMap = glowingEyesMap;
        try {
            updateTexture();
        } catch (Exception ignored) {
            /*
             this only happens when the game is running on the wrong thread,
             which only happens when we are running on the wrong side (server instead of client)
             or when the method is called from the wrong thread
             since I have no idea how to check that, I'll just try catch it lol
            */
        }
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
        if (resourceLocation != null) {
            Minecraft.getInstance().getTextureManager().release(resourceLocation);
        }

        BufferedImage eyeOverlayTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        for (Point point : glowingEyesMap.keySet()) {
            Color color = glowingEyesMap.get(point);
            eyeOverlayTexture.setRGB(point.x + 8, point.y + 8, color.getRGB());
        }
        DynamicTexture dynamicTexture = new DynamicTexture(Util.toNativeImage(eyeOverlayTexture));
        Minecraft.getInstance().getTextureManager().register(
                resourceLocation = new ResourceLocation(GlowingEyes.MOD_ID + ":eyes_texture" + UUID.randomUUID()),
                dynamicTexture
        );
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        setToggledOn(tag.getBoolean("toggledOn"));
        setGlowingEyesMap(Util.deserializeMap(tag.getByteArray("glowingEyesMap")));
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("toggledOn", isToggledOn());
        tag.putByteArray("glowingEyesMap", Util.serializeMap(getGlowingEyesMap()));
    }
}
