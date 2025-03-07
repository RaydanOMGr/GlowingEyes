package me.andreasmelone.glowingeyes.client.util;

import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

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
        guiGraphics.blit(RenderType::guiTextured, backgroundTexture, x, y, 0, 0, width, height, 256, 256);
    }

    public static WidgetSprites createSprites(String namespace, String location1, String location2) {
        return new WidgetSprites(Util.id(namespace, location1), Util.id(namespace, location2));
    }

    public static WidgetSprites createSprites(String namespace, String location1, String location2, String location3) {
        return new WidgetSprites(Util.id(namespace, location1), Util.id(namespace, location2), Util.id(namespace, location3));
    }

    public static WidgetSprites createSprites(String namespace, String enabled, String disabled, String enabledHighlighted, String disabledHighlighted) {
        return new WidgetSprites(Util.id(namespace, enabled), Util.id(namespace, disabled), Util.id(namespace, enabledHighlighted), Util.id(namespace, disabledHighlighted));
    }

    public static void drawWrappedText(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int maxWidth, int color) {
        List<FormattedCharSequence> lines = font.split(text, maxWidth);
        int lineHeight = font.lineHeight;

        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawCenteredString(font, lines.get(i), x, y + (i * lineHeight), color);
        }
    }
}
