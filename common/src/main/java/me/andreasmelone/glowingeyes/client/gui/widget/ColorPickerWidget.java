package me.andreasmelone.glowingeyes.client.gui.widget;

import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ColorPickerWidget extends AbstractWidget implements GuiEventListener {
    private static final int CURSOR_OFFSET_X = 5;
    private static final int CURSOR_OFFSET_Y = 5; // I am not sure if those are the right names lol

    private static final int HOVERED_COLOR = 0xFFFFFFFF;
    private static final int INACTIVE_COLOR = 0xFF000000;

    private static final float INCREMENT_CTRL_PRESS_FACTOR = 3.5f;
    private static final float INCREMENT_NORMAL_FACTOR = 1.0f;
    private static final float INCREMENT = 1 / 255f;

    private float hue;
    private float brightness; // [0.0, 1.0]
    private float saturation; // [0.0, 1.0]
    private boolean isShiftPressed = false;
    private final List<Consumer<ColorPickerWidget>> onChangeListeners = new ArrayList<>();

    public ColorPickerWidget(int x, int y, int width, int height, float hue,
                             float brightness, float saturation) {
        super(x, y, width, height, Component.empty());
        this.hue = hue;
        this.brightness = brightness;
        this.saturation = saturation;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float deltaTime) {
        GuiUtil.drawColorSquare(
                ctx,
                this.getX(), this.getY(),
                this.width, this.height,
                Color.HSBtoRGB(this.hue, 1.0f, 1.0f)
        );
        GuiUtil.drawOutline(
                ctx,
                this.getX() - 1, this.getY() - 1,
                this.width + 2, this.height + 2,
                this.isHoveredOrFocused() ? HOVERED_COLOR : INACTIVE_COLOR
        );

        ctx.pose().pushMatrix();
        ctx.pose().translate(this.getCursorX(), this.getCursorY());
        ctx.pose().translate(-CURSOR_OFFSET_X + (2 * (1.0f - this.saturation)), - CURSOR_OFFSET_Y + (2 * (this.brightness)));
        ctx.blit(
                RenderPipelines.GUI_TEXTURED,
                TextureLocations.CURSOR,
                0, 0,
                0, 0,
                8, 8,
                8, 8
        );
        ctx.pose().popMatrix();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if(event.button() == 0 && this.isInbounds((int) event.x(), (int) event.y()))
            return this.mouseDragged(event, 0, 0);
        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if(this.isInbounds((int) event.x(), (int) event.y())) {
            this.setCursorX((float) event.x());
            this.setCursorY((float) event.y());

            this.triggerChange();
        } else {
            float newX = (float) Math.max(this.getX(), Math.min(event.x(), this.getX() + this.width));
            float newY = (float) Math.max(this.getY(), Math.min(event.y(), this.getY() + this.height));
            this.setCursorX(newX);
            this.setCursorY(newY);

            this.triggerChange();
        }
        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.isInbounds((int) mouseX, (int) mouseY)) {
            if(this.isShiftPressed) {
                scrollX = scrollX + scrollY;
                scrollY = 0;
            }
            this.saturation = (float)Math.clamp(this.saturation + scrollX / 1500f, 0.0f, 1.0f);
            this.brightness = (float)Math.clamp(this.brightness + scrollY / 1500f, 0.0f, 1.0f);
            this.triggerChange();
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        int modifiers = event.modifiers();
        if(keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
            this.isShiftPressed = true;
        }
        boolean isCtrlPressed = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
        float increment = INCREMENT * (isCtrlPressed ? INCREMENT_CTRL_PRESS_FACTOR : INCREMENT_NORMAL_FACTOR);
        if(keyCode == GLFW.GLFW_KEY_UP) {
            this.brightness = Math.clamp(this.brightness + increment, 0.0f, 1.0f);
            this.triggerChange();
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_DOWN) {
            this.brightness = Math.clamp(this.brightness - increment, 0.0f, 1.0f);
            this.triggerChange();
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_LEFT) {
            this.saturation = Math.clamp(this.saturation - increment, 0.0f, 1.0f);
            this.triggerChange();
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_RIGHT) {
            this.saturation = Math.clamp(this.saturation + increment, 0.0f, 1.0f);
            this.triggerChange();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if(event.key() == GLFW.GLFW_KEY_LEFT_SHIFT) {
            this.isShiftPressed = false;
        }
        return super.keyReleased(event);
    }

    public float getCursorX() {
        return this.getX() + (this.saturation * this.width);
    }

    public void setCursorX(float cursorX) {
        // cursor = posX + (x * width)  | - posX
        // cursor - posX = x * width    | : width
        // (cursor - posX) / width = x
        this.saturation = (cursorX - this.getX()) / this.width;
    }

    public float getCursorY() {
        return this.getY() + ((1.0f - this.brightness) * this.height);
    }

    public void setCursorY(float cursorY) {
        // y + ((1.0 - b) * h) = c   | -y
        // (1.0 - b) * h = c - y     | : h
        // 1.0 - b = (c - y) / h     | - 1.0
        // - b = (c - y) / h - 1.0   | : -1
        // b = -((c - y) / h - 1.0)
        this.brightness = -((cursorY - this.getY()) / this.height - 1.0f);
    }

    public float getHue() {
        return this.hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public float getBrightness() {
        return this.brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getSaturation() {
        return this.saturation;
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
        return x >= this.getX() && y >= this.getY()
                && x <= this.getX() + this.width
                && y <= this.getY() + this.height;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}
