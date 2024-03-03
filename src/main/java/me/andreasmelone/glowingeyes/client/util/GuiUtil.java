package me.andreasmelone.glowingeyes.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class GuiUtil {
    /**
     * Draw the background texture
     * @param x The x position of the texture (usually named guiLeft)
     * @param y The y position of the texture (usually named guiTop)
     * @param width The width of the texture (usually named xSize)
     * @param height The height of the texture (usually named ySize)
     */
    public static void drawBackground(PoseStack poseStack, ResourceLocation backgroundTexture, int x, int y, int width, int height) {
        // Use the Minecraft class to get the texture manager
        RenderSystem.setShaderTexture(0, backgroundTexture);
        // Draw the background texture
        Gui.blit(poseStack, x, y, 0, 0, width, height, 256, 256);
    }

    public static Color invertColor(Color color) {
        int red = 255 - color.getRed();
        int green = 255 - color.getGreen();
        int blue = 255 - color.getBlue();

        return new Color(red, green, blue);
    }

    public static int ARGBtoRGBA(int argb) {
        int alpha = (argb >> 24) & 0xFF;
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;

        return (blue << 24) | (red << 16) | (green << 8) | alpha;
    }

    public static float getRedFromRGB(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static float getGreenFromRGB(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static float getBlueFromRGB(int rgb) {
        return rgb & 0xFF;
    }
}
