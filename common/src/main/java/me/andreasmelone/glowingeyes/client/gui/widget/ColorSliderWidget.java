package me.andreasmelone.glowingeyes.client.gui.widget;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.client.util.color.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ColorSliderWidget extends AbstractWidget implements GuiEventListener {
    private float hue;
    private ResourceLocation colorSliderTexture;

    private final List<Consumer<ColorSliderWidget>> onChangeListeners = new ArrayList<>();

    public ColorSliderWidget(int x, int y, int width, int height, float hue) {
        super(x, y, width, height, Component.empty());
        this.hue = hue;
    }

    @Override
    public void renderWidget(@NotNull PoseStack poseStack, int mouseX, int mouseY, float deltaTime) {
        RenderSystem.setShaderTexture(0, getColorSliderTexture());
        blit(
                poseStack,
                getX(), getY(),
                0, 0,
                width, height,
                width, height
        );

        RenderSystem.setShaderTexture(0, TextureLocations.BRIGHTNESS_CURSOR);
        blit(
                poseStack,
                getX() - (2 * (width / 16)), (int) (getCursor() - ((2 + (1 - 2) * this.hue) * ((float) width / 16))),
                width + (4 * (width / 16)), height / 3,
                0, 0,
                16, 16,
                16, 16
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isInbounds((int) mouseX, (int) mouseY)) return mouseDragged(mouseX, mouseY, button, 0, 0);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(mouseY >= getY() && mouseY <= getY() + height) {
            this.setCursor((int) mouseY);
            triggerChange();
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private boolean isInbounds(int x, int y) {
        return x >= this.getX() && y >= this.getY()
                && x <= this.getX() + this.width
                && y <= this.getY() + this.height;
    }

    public int getCursor() {
        return (int) (this.getY() + (1.0f - this.hue) * this.height);
    }

    public void setCursor(int cursorY) {
        this.hue = 1.0f - (float) (cursorY - this.getY()) / this.height;
    }

    public float getHue() {
        return hue;
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
        NativeImage image = new NativeImage(width, height, true);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                float ratioY = 1.0f - ((float) y / height);
                image.setPixelRGBA(x, y, ColorUtil.HSBtoBGR(ratioY, 1.0f, 1.0f));
            }
        }
        return image;
    }

    private ResourceLocation getColorSliderTexture() {
        if(colorSliderTexture == null) {
            NativeImage image = createColorSliderTexture();
            colorSliderTexture = Minecraft.getInstance().getTextureManager().register(
                    GlowingEyes.MOD_ID + "_color_slider",
                    new DynamicTexture(image)
            );
        }
        return colorSliderTexture;
    }

    private void clearColorSliderTexture() {
        Minecraft.getInstance().getTextureManager().release(colorSliderTexture);
        this.colorSliderTexture = null;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}