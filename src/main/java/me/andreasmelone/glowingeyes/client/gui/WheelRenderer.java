package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WheelRenderer {
    public final static int WHEEL_RADIUS = 256;
    public final static float SCALE = WHEEL_RADIUS / 100.0f;

    static ResourceLocation colorWheel = null;
    public static void renderColorWheel(PoseStack posestack, int colorWheelX, int colorWheelY) {
        renderColorWheel(posestack, colorWheelX, colorWheelY, 100, 100);
    }

    public static void renderColorWheel(PoseStack posestack, int colorWheelX, int colorWheelY,
                                        int colorWheelWidth, int colorWheelHeight) {
        if(colorWheel == null) {
            NativeImage image = getNativeImage();
            colorWheel = Minecraft.getInstance().getTextureManager().register(
                    GlowingEyes.MOD_ID + ".color_wheel", new DynamicTexture(image)
            );
            renderColorWheel(posestack, colorWheelX, colorWheelY, colorWheelWidth, colorWheelHeight);
        } else {
            RenderSystem.setShaderTexture(0, colorWheel);
            GuiComponent.blit(
                    posestack,
                    colorWheelX, colorWheelY,
                    0, 0,
                    colorWheelWidth, colorWheelHeight,
                    colorWheelWidth, colorWheelHeight
            );
        }
    }

    public static ResourceLocation getColorWheel() {
        if(colorWheel == null) {
            NativeImage image = getNativeImage();
            colorWheel = Minecraft.getInstance().getTextureManager().register(
                    GlowingEyes.MOD_ID + ".color_wheel",
                    new DynamicTexture(image)
            );
        }
        return colorWheel;
    }

    public static void resetColorWheel() {
        Minecraft.getInstance().getTextureManager().release(colorWheel);
        colorWheel = null;
    }

    @NotNull
    private static NativeImage getNativeImage() {
        NativeImage nativeImage = new NativeImage(WHEEL_RADIUS * 2, WHEEL_RADIUS * 2, true);
        for(int x = 0; x < WHEEL_RADIUS * 2; x++) {
            for(int y = 0; y < WHEEL_RADIUS * 2; y++) {
                int dx = x - WHEEL_RADIUS;
                int dy = y - WHEEL_RADIUS;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if(distance < WHEEL_RADIUS) {
                    // use the getColorAt method to get the color at the current position
                    int color = getColorAt(x, y);
                    nativeImage.setPixelRGBA(x, y, color);
                } else {
                    nativeImage.setPixelRGBA(x, y, 0);
                }
            }
        }
        return nativeImage;
    }

    public static int getColorAt(int x, int y) {
        int dx = x - WHEEL_RADIUS;
        int dy = y - WHEEL_RADIUS;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if(distance < WHEEL_RADIUS) {
            float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
            if(angle < 0) {
                angle += 360;
            }
            float saturation = distance / WHEEL_RADIUS;
            float value = 1;
            return Color.HSBtoRGB(angle / 360, saturation, value);
        } else {
            return 0;
        }
    }
}
