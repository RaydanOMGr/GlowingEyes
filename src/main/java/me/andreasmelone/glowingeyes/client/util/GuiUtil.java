package me.andreasmelone.glowingeyes.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiUtil {
    // Make a function to draw the background texture for the GUI
    public static void drawBackground(int x, int y, int width, int height) {
        // Use the Minecraft class to get the texture manager
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureLocations.UI_BACKGROUND);
        // Draw the background texture
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
    }
}
