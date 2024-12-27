package me.andreasmelone.glowingeyes.client.gui.widget;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.client.util.color.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ColorPickerWidget extends AbstractWidget implements Widget, GuiEventListener {
    private float hue;
    private float brightness; // [0.0, 1.0]
    private float saturation; // [0.0, 1.0]
    private ResourceLocation colorSquareTexture;

    private final List<Consumer<ColorPickerWidget>> onChangeListeners = new ArrayList<>();

    public ColorPickerWidget(int x, int y, int width, int height, float hue,
                             float brightness, float saturation) {
        super(x, y, width, height, Component.empty());
        this.hue = hue;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float deltaTime) {
        RenderSystem.setShaderTexture(0, getColorSquareTexture());
        blit(
                poseStack,
                x, y,
                0, 0,
                width, height,
                width, height
        );

        RenderSystem.setShaderTexture(0, TextureLocations.CURSOR);
        blit(
                poseStack,
                (int) (getCursorX() - 5 + (2 * (1.0f - this.saturation))),
                (int) (getCursorY() - 5 + (2 * (this.brightness))),
                0, 0,
                8, 8,
                8, 8
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isInbounds((int) mouseX, (int) mouseY))
            return this.mouseDragged(mouseX, mouseY, button, 0, 0);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int intX = (int) mouseX;
        int intY = (int) mouseY;
        if(isInbounds(intX, intY)) {
            this.setCursorX(intX);
            this.setCursorY(intY);

            triggerChange();
        } else {
            int newX = (int) Math.max(this.x, Math.min(mouseX, this.x + this.width));
            int newY = (int) Math.max(this.y, Math.min(mouseY, this.y + this.height));
            this.setCursorX(newX);
            this.setCursorY(newY);

            triggerChange();
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public int getCursorX() {
        return (int) (this.x + (this.saturation * this.width));
    }

    public void setCursorX(int cursorX) {
        // cursor = posX + (x * width)  | - posX
        // cursor - posX = x * width    | : width
        // (cursor - posX) / width = x
        this.saturation = (float) (cursorX - x) / width;
    }

    public int getCursorY() {
        return (int) (this.y + ((1.0f - this.brightness) * this.height));
    }

    public void setCursorY(int cursorY) {
        // y + ((1.0 - b) * h) = c   | -y
        // (1.0 - b) * h = c - y     | : h
        // 1.0 - b = (c - y) / h     | - 1.0
        // - b = (c - y) / h - 1.0   | : -1
        // b = -((c - y) / h - 1.0)
        this.brightness = (float) -((float) (cursorY - this.y) / this.height - 1.0f);
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
        this.clearColorSquareTexture();
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void onChange(Consumer<ColorPickerWidget> listener) {
        this.onChangeListeners.add(listener);
    }

    private void triggerChange() {
        this.onChangeListeners.forEach((l) -> l.accept(this));
    }

    private boolean isInbounds(int x, int y) {
        return x >= this.x && y >= this.y
                && x <= this.x + this.width
                && y <= this.y + this.height;
    }

    private NativeImage createColorGradientImage() {
        NativeImage image = new NativeImage(width, height, true);
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                float saturation = (float) x / this.width;
                float brightness = 1.0f - (float) y / this.height;

                image.setPixelRGBA(x, y, ColorUtil.HSBtoBGR(hue, saturation, brightness));
            }
        }

        return image;
    }

    private ResourceLocation getColorSquareTexture() {
        if(colorSquareTexture == null) {
            NativeImage image = createColorGradientImage();
            colorSquareTexture = Minecraft.getInstance().getTextureManager().register(
                    GlowingEyes.MOD_ID + "_color_square",
                    new DynamicTexture(image)
            );
        }
        return colorSquareTexture;
    }

    private void clearColorSquareTexture() {
        Minecraft.getInstance().getTextureManager().release(colorSquareTexture);
        this.colorSquareTexture = null;
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {
    }
}
