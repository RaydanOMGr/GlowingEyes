package me.andreasmelone.glowingeyes.client.gui.widget;

import com.mojang.blaze3d.platform.NativeImage;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.client.util.color.ColorUtil;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ColorSliderWidget extends AbstractWidget implements GuiEventListener {
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 16;

    // texture width is the width of the actual texture in our case
    // the sprite width (aka UI_WIDTH) is the width of the sprite
    // same applies to height
    private static final int SPRITE_WIDTH = 16;
    private static final int SPRITE_HEIGHT = 3;
    private static final int CURSOR_OFFSET_Y = 2;
    private static final int SPRITE_OFFSET_X = -1;


    private float hue;
    private ResourceLocation colorSliderTexture;

    private final List<Consumer<ColorSliderWidget>> onChangeListeners = new ArrayList<>();

    public ColorSliderWidget(int x, int y, int width, int height, float hue) {
        super(x, y, width, height, Component.empty());
        this.hue = hue;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float deltaTime) {
        ctx.blit(
                RenderType::guiTextured,
                this.getColorSliderTexture(),
                this.getX(), this.getY(),
                0, 0,
                this.width, this.height,
                this.width, this.height
        );

        int newSpriteWidth = SPRITE_WIDTH + (SPRITE_OFFSET_X * 2);
        int spriteRatio = newSpriteWidth / SPRITE_HEIGHT;
        float xScale = (float) this.width / newSpriteWidth;
        float yScale = (float) (Math.floor((float) this.width / spriteRatio) / SPRITE_HEIGHT);

        ctx.pose().pushPose();
        ctx.pose().translate(this.getX(), this.getCursor(), 0.0f);
        ctx.pose().scale(xScale, yScale, 1.0f);
        ctx.pose().translate(SPRITE_OFFSET_X, -1 * (CURSOR_OFFSET_Y + (1 - CURSOR_OFFSET_Y) * this.hue), 0);
        ctx.blit(
                RenderType::guiTextured,
                TextureLocations.BRIGHTNESS_CURSOR,
                0, 0,
                0, 0,
                SPRITE_WIDTH, SPRITE_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
        ctx.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == 0 && this.isInbounds((int) mouseX, (int) mouseY)) return this.mouseDragged(mouseX, mouseY, button, 0, 0);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.setCursor(Math.clamp((int) mouseY, this.getY(), this.getY() + this.height));
        this.triggerChange();

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.isInbounds((int) mouseX, (int) mouseY)) {
            this.hue = (float)Math.clamp(this.hue + scrollY / 1500f, 0.0f, 1.0f);
            this.triggerChange();
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean isInbounds(int x, int y) {
        return x >= this.getX() && y >= this.getY()
                && x <= this.getX() + this.width
                && y <= this.getY() + this.height;
    }

    public float getCursor() {
        return (this.getY() + (1.0f - this.hue) * this.height);
    }

    public void setCursor(float cursorY) {
        this.hue = 1.0f - (cursorY - this.getY()) / this.height;
    }

    public float getHue() {
        return this.hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public void onChange(Consumer<ColorSliderWidget> listener) {
        this.onChangeListeners.add(listener);
    }

    private void triggerChange() {
        this.onChangeListeners.forEach((l) -> l.accept(this));
    }

    private NativeImage createColorSliderTexture() {
        NativeImage image = new NativeImage(this.width, this.height, true);
        for(int y = 0; y < this.height; y++) {
            for(int x = 0; x < this.width; x++) {
                float ratioY = 1.0f - ((float) y / this.height);

                Color color = new Color(ColorUtil.HSBtoBGR(ratioY, 1.0f, 1.0f));
                image.setPixel(x, y, new Color(color.getBlue(), color.getGreen(), color.getRed()).getRGB());
            }
        }
        return image;
    }

    private ResourceLocation getColorSliderTexture() {
        if(this.colorSliderTexture == null) {
            NativeImage image = this.createColorSliderTexture();
            this.colorSliderTexture = Util.id(GlowingEyes.MOD_ID, "color_slider");
            Minecraft.getInstance().getTextureManager().register(
                    this.colorSliderTexture,
                    new DynamicTexture(image)
            );
        }
        return this.colorSliderTexture;
    }

    private void clearColorSliderTexture() {
        Minecraft.getInstance().getTextureManager().release(this.colorSliderTexture);
        this.colorSliderTexture = null;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}