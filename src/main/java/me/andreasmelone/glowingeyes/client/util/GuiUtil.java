package me.andreasmelone.glowingeyes.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GuiUtil {
    /**
     * Draw the background texture
     * @param x The x position of the texture (usually named guiLeft)
     * @param y The y position of the texture (usually named guiTop)
     * @param width The width of the texture (usually named xSize)
     * @param height The height of the texture (usually named ySize)
     */
    public static void drawBackground(GuiGraphics guiGraphics, ResourceLocation backgroundTexture, int x, int y, int width, int height) {
        // Draw the background texture
        guiGraphics.blit(backgroundTexture, x, y, 0, 0, width, height, 256, 256);
    }

    public static NativeImage toNativeImage(BufferedImage image) {
        NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), true);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                // native textures use BGR instead of RGB so we have to convert the color
                Color bgr = new Color(argb);
                bgr = new Color(bgr.getBlue(), bgr.getGreen(), bgr.getRed());

                nativeImage.setPixelRGBA(x, y, bgr.getRGB());
            }
        }

        return nativeImage;
    }
}
