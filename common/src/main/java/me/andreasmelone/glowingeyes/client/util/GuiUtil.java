package me.andreasmelone.glowingeyes.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import me.andreasmelone.glowingeyes.client.render.ShaderManager;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

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
        guiGraphics.blit(backgroundTexture, x, y, 0, 0, width, height, 256, 256);
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

    public static void blitTintedSprite(GuiGraphics ctx, ResourceLocation spriteLocation, int x, int y, int width, int height, int color) {
        Minecraft mc = Minecraft.getInstance();
        GuiSpriteManager sprites = mc.getGuiSprites();

        TextureAtlasSprite sprite = sprites.getSprite(spriteLocation);

        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Matrix4f matrix4f = ctx.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, 0f).setUv(sprite.getU0(), sprite.getV0()).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y + height, 0f).setUv(sprite.getU0(), sprite.getV1()).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x + width, (float)y + height, 0f).setUv(sprite.getU1(), sprite.getV1()).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x + width, (float)y, 0f).setUv(sprite.getU1(), sprite.getV0()).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
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
        Minecraft mc = Minecraft.getInstance();
        GuiSpriteManager sprites = mc.getGuiSprites();

        TextureAtlasSprite sprite = sprites.getSprite(TextureLocations.CURSOR);

        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.setShader(ShaderManager::colorSquare);
        Matrix4f matrix4f = ctx.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, 0f).setUv(0, 0).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y + height, 0f).setUv(0, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x + width, (float)y + height, 0f).setUv(1, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x + width, (float)y, 0f).setUv(1, 0).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
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
        Minecraft mc = Minecraft.getInstance();
        GuiSpriteManager sprites = mc.getGuiSprites();

        TextureAtlasSprite sprite = sprites.getSprite(TextureLocations.CURSOR);

        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.setShader(ShaderManager::hueBar);
        Matrix4f matrix4f = ctx.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, 0f).setUv(0, 0).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y + height, 0f).setUv(0, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x + width, (float)y + height, 0f).setUv(1, 1).setColor(color);
        bufferbuilder.addVertex(matrix4f, (float)x + width, (float)y, 0f).setUv(1, 0).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
