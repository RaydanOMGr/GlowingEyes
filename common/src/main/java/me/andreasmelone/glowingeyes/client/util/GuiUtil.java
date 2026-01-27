package me.andreasmelone.glowingeyes.client.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.andreasmelone.glowingeyes.client.render.PipelineManager;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    public static void drawBackground(GuiGraphics guiGraphics, Identifier backgroundTexture, int x, int y, int width, int height) {
        // Draw the background texture
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture, x, y, 0, 0, width, height, 256, 256);
    }

    public static void drawTransparentBlack(GuiGraphics ctx) {
        ctx.fill(0, 0, ctx.guiWidth(), ctx.guiHeight(), 0xBB000000);
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

    public static int drawWrappedText(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int maxWidth, int color) {
        List<FormattedCharSequence> lines = font.split(text, maxWidth);
        int lineHeight = font.lineHeight;

        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawCenteredString(font, lines.get(i), x, y + (i * lineHeight), color);
        }

        return lines.size();
    }

    public static void drawCursorPos(GuiGraphics ctx, Font font, int mouseX, int mouseY) {
        ctx.drawString(font, "x: " + mouseX + ", y: " + mouseY, 10, 10, Color.WHITE.getRGB());
    }

    public static void blitTintedSprite(GuiGraphics ctx, RenderPipeline pipeline, Identifier spriteLocation, int x, int y, int width, int height, int color) {
        ctx.blitSprite(pipeline, spriteLocation, x, y, width, height, color);
    }

    /**
     * Draws a "color" square, commonly used in color selection tools.<p>
     * The top-left corner (0, 0) will have a saturation of 0.0 and a brightness of 1.0<p>
     * The top-right corner (0, 1) will have a saturation of 1.0 and a brightness of 1.0<p>
     * The bottom-left corner (1, 0) will have a saturation of 0.0 and a brightness of 0.0<p>
     * The bottom-right corner (1, 1) will have a saturation of 1.0 and a brightness of 0.0<p>
     *
     * @implNote The colorSquare RenderType takes a texture, but this texture is never actually used by the shader.
     *           This is done in order to acquire the normalized coordinates of the current pass from the fragment shader.
     *
     * @param ctx The {@link GuiGraphics} object
     * @param x the x coordinate at which the top-left corner of the drawn square will be located
     * @param y the y coordinate at which the top-left corner of the drawn square will be located
     * @param width the width of the bar
     * @param height the height of the bar
     * @param color an ARGB color, from which the hue and alpha for the drawn square will be taken.
     *              The saturation and brightness are ignored.
     */
    public static void drawColorSquare(GuiGraphics ctx, int x, int y, int width, int height, int color) {
        ctx.blit(
                PipelineManager.COLOR_SQUARE_SHADER,
                TextureLocations.CURSOR,    // random texture, it will never get rendered anyway
                x, y, 0, 0,  // this is a dirty hack to acquire normalized coordinates in the shader
                width, height, width, height,
                color
        );
    }

    /**
     * Draws a vertical bar that includes all hues from 0.0 to 1.0
     *
     * @implNote The hueBar RenderType takes a texture, but this texture is never actually used by the shader.
     *           This is done in order to acquire the normalized coordinates of the current pass from the fragment shader.
     *
     * @param ctx The {@link GuiGraphics} object
     * @param x the x coordinate at which the top-left corner of the drawn bar will be located
     * @param y the y coordinate at which the top-left corner of the drawn bar will be located
     * @param width the width of the bar
     * @param height the height of the bar
     * @param color an ARGB color, from which the saturation, brightness and alpha for the drawn bar will be taken.
     *              The hue of this color does not affect the final output, meaning that 0xFFFF0000 and 0xFF00FF00 draw the exact same thing.
     */
    public static void drawHueBar(GuiGraphics ctx, int x, int y, int width, int height, int color) {
        ctx.blit(
                PipelineManager.HUE_BAR_SHADER,
                TextureLocations.CURSOR, // similarly to the color square, just a random texture, never used or rendered by the actual shader
                x, y, 0, 0,
                width, height, width, height,
                color
        );
    }

    public static void drawOutline(GuiGraphics ctx, int x, int y, int width, int height, int color) {
        ctx.fill(x, y, x + width, y + 1, color);
        ctx.fill(x, y + height - 1, x + width, y + height, color);
        ctx.fill(x, y + 1, x + 1, y + height - 1, color);
        ctx.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }
}
