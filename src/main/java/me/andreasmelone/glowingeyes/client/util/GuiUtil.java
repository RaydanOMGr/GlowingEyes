package me.andreasmelone.glowingeyes.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

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
}
