package me.andreasmelone.glowingeyes.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiUtil {
    /**
     * Draw the background texture
     * @param x The x position of the texture (usually named guiLeft)
     * @param y The y position of the texture (usually named guiTop)
     * @param width The width of the texture (usually named xSize)
     * @param height The height of the texture (usually named ySize)
     */
    public static void drawBackground(ResourceLocation backgroundTexture, int x, int y, int width, int height) {
        // Use the Minecraft class to get the texture manager
        Minecraft.getMinecraft().getTextureManager().bindTexture(backgroundTexture);
        // Draw the background texture
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, 256, 256);
    }

    /**
     * Draw a scaled texture
     * @param x The x position of the texture on the screen
     * @param y The y position of the texture on the screen
     * @param u The x position of the texture in the texture file
     * @param v The y position of the texture in the texture file
     * @param uWidth The width of the texture in the texture file
     * @param vHeight The height of the texture in the texture file
     * @param width The width of the texture on the screen
     * @param height The height of the texture on the screen
     * @param tileWidth The width of the texture image
     * @param tileHeight The height of the texture image
     */
    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight,
                                                     int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0.0D).tex(u * f, (v + (float)vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0D).tex((u + (float)uWidth) * f, (v + (float)vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y, 0.0D).tex(((u + (float)uWidth) * f), v * f1).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }
}
