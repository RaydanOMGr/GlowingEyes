package me.andreasmelone.glowingeyes.client.gui.widget;

import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

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

    private static final int HOVERED_COLOR = 0xFFFFFFFF;
    private static final int INACTIVE_COLOR = 0xFF000000;

    private static final float INCREMENT = 1 / 360f;
    private static final float INCREMENT_NORMAL_FACTOR = 1.0f;
    private static final float INCREMENT_CTRL_PRESSED_FACTOR = 2.5f;

    private float hue;
    private final List<Consumer<ColorSliderWidget>> onChangeListeners = new ArrayList<>();

    public ColorSliderWidget(int x, int y, int width, int height, float hue) {
        super(x, y, width, height, Component.empty());
        this.hue = hue;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float deltaTime) {
        GuiUtil.drawHueBar(
                ctx,
                this.getX(), this.getY(),
                this.width, this.height,
                Color.HSBtoRGB(this.hue, 1.0f, 1.0f)
        );
        ctx.renderOutline(
                this.getX() - 1, this.getY() - 1,
                this.width + 2, this.height + 2,
                this.isHoveredOrFocused() ? HOVERED_COLOR : INACTIVE_COLOR
        );

        int newSpriteWidth = SPRITE_WIDTH + (SPRITE_OFFSET_X * 2);
        int spriteRatio = newSpriteWidth / SPRITE_HEIGHT;
        float xScale = Math.abs((float) this.width / newSpriteWidth);
        float yScale = Math.abs((float) (Math.floor((float) this.width / spriteRatio) / SPRITE_HEIGHT));

        ctx.pose().pushPose();
        ctx.pose().translate(this.getX(), this.getCursor(), 0.0f);
        ctx.pose().scale(xScale, yScale, 1.0f);
        ctx.pose().translate(SPRITE_OFFSET_X, -1 * (CURSOR_OFFSET_Y + (1 - CURSOR_OFFSET_Y) * this.hue), 0);
        ctx.blit(
                TextureLocations.BRIGHTNESS_CURSOR,
                0, 0,
                0, 0,
                SPRITE_WIDTH, SPRITE_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
        ctx.pose().popPose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean isCtrlPressed = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
        float increment = INCREMENT * (isCtrlPressed ? INCREMENT_CTRL_PRESSED_FACTOR : INCREMENT_NORMAL_FACTOR);
        if(keyCode == GLFW.GLFW_KEY_UP) {
            this.hue = ((this.hue + increment) % 1.0f + 1.0f) % 1.0f;
            this.triggerChange();
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_DOWN) {
            this.hue = ((this.hue - increment) % 1.0f + 1.0f) % 1.0f;
            this.triggerChange();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}