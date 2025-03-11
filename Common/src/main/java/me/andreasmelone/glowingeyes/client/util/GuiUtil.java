package me.andreasmelone.glowingeyes.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.awt.image.BufferedImage;
import java.util.List;

public class GuiUtil {
    /**
     * Draw the background texture
     * @param x The x position of the texture (usually named guiLeft)
     * @param y The y position of the texture (usually named guiTop)
     * @param width The width of the texture (usually named xSize)
     * @param height The height of the texture (usually named ySize)
     */
    public static void drawBackground(PoseStack poseStack, ResourceLocation backgroundTexture, int x, int y, int width, int height) {
        // Draw the background texture
        RenderSystem.setShaderTexture(0, backgroundTexture);
        Gui.blit(poseStack, x, y, 0, 0, width, height, 256, 256);
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

    public static void drawWrappedText(PoseStack poseStack, Font font, Component text, int x, int y, int maxWidth, int color) {
        List<FormattedCharSequence> lines = font.split(text, maxWidth);
        int lineHeight = font.lineHeight;

        for (int i = 0; i < lines.size(); i++) {
            Gui.drawCenteredString(poseStack, font, lines.get(i), x, y + (i * lineHeight), color);
        }
    }
}
