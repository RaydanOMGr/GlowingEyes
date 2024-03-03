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

    public static int HSVtoBGR(double hue, double saturation, double value) {
        int[] bgr = new int[3];

        double c = value * saturation;
        double x = c * (1 - Math.abs((hue / 60) % 2 - 1));
        double m = value - c;

        double r1, g1, b1;

        if (hue >= 0 && hue < 60) {
            r1 = c;
            g1 = x;
            b1 = 0;
        } else if (hue >= 60 && hue < 120) {
            r1 = x;
            g1 = c;
            b1 = 0;
        } else if (hue >= 120 && hue < 180) {
            r1 = 0;
            g1 = c;
            b1 = x;
        } else if (hue >= 180 && hue < 240) {
            r1 = 0;
            g1 = x;
            b1 = c;
        } else if (hue >= 240 && hue < 300) {
            r1 = x;
            g1 = 0;
            b1 = c;
        } else {
            r1 = c;
            g1 = 0;
            b1 = x;
        }

        bgr[0] = (int) ((r1 + m) * 255);
        bgr[1] = (int) ((g1 + m) * 255);
        bgr[2] = (int) ((b1 + m) * 255);

        return new Color(bgr[2], bgr[1], bgr[0]).getRGB();
    }

    public static int HSBtoBGR(float hue, float saturation, float brightness) {
        return HSVtoBGR(hue * 360, saturation, brightness);
    }

    public static int BGRtoHSB(int bgr) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(
                (bgr >> 16) & 0xFF,
                (bgr >> 8) & 0xFF,
                bgr & 0xFF,
                hsb
        );
        return new Color(
                HSBtoBGR(hsb[0], hsb[1], hsb[2])
        ).getRGB();
    }

    public static int BGRtoHSB(int blue, int green, int red) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(red, green, blue, hsb);
        return new Color(
                HSBtoBGR(hsb[0], hsb[1], hsb[2])
        ).getRGB();
    }

    public static int RGBtoHSB(int rgb) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(
                (rgb >> 16) & 0xFF,
                (rgb >> 8) & 0xFF,
                rgb & 0xFF,
                hsb
        );
        return new Color(
                HSBtoBGR(hsb[0], hsb[1], hsb[2])
        ).getRGB();
    }
}
