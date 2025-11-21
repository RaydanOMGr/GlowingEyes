package me.andreasmelone.glowingeyes.client.gui.widget;

import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class CursorSpaceWidget extends AbstractWidget implements GuiEventListener {
    private static final int PRIMARY_MOUSE_BUTTON = 0;
    private static final int SECONDARY_MOUSE_BUTTON = 1;
    private static final int MIDDLE_MOUSE_BUTTON = 2;

    private static final float SENSITIVITY = 2.5f;

    private static final int CURSOR_SIZE = 8;
    private static final int CURSOR_OFFSET = CURSOR_SIZE / 2;

    private double cursorX;
    private double cursorY;

    protected final MouseMovedCallback mouseMovedCallback;
    protected final MouseClickedCallback mouseClickedCallback;

    public CursorSpaceWidget(int x, int y, int width, int height, MouseMovedCallback mouseMovedCallback, MouseClickedCallback mouseClickedCallback) {
        super(x, y, width, height, Component.empty());
        this.mouseMovedCallback = mouseMovedCallback;
        this.mouseClickedCallback = mouseClickedCallback;
        this.cursorX = x + CURSOR_OFFSET;
        this.cursorY = y + CURSOR_OFFSET;
    }

    public CursorSpaceWidget(int x, int y, int width, int height, MouseMovedCallback mouseMovedCallback) {
        this(x, y, width, height, mouseMovedCallback, (event, button) -> false);
    }

    public CursorSpaceWidget(int x, int y, int width, int height, MouseClickedCallback mouseClickedCallback) {
        this(x, y, width, height, (mouseX, mouseY) -> {}, mouseClickedCallback);
    }

    public CursorSpaceWidget(int x, int y, int width, int height) {
        this(x, y, width, height, (mouseX, mouseY) -> {}, (event, button) -> false);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTick) {
        if(!this.isUsed()) return;
        ctx.pose().pushMatrix();
        ctx.pose().translate((float) this.cursorX, (float) this.cursorY);
        ctx.pose().translate(-CURSOR_OFFSET, -CURSOR_OFFSET);
        ctx.blit(
                RenderPipelines.CROSSHAIR,
                TextureLocations.CURSOR,
                0, 0,
                0, 0,
                CURSOR_SIZE, CURSOR_SIZE,
                CURSOR_SIZE, CURSOR_SIZE
        );
        ctx.pose().popMatrix();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        int modifiers = event.modifiers();
        if(keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
            boolean isAltPressed = (modifiers & GLFW.GLFW_MOD_ALT) != 0;
            return this.triggerClick(isAltPressed ? SECONDARY_MOUSE_BUTTON : PRIMARY_MOUSE_BUTTON);
        }
        if(keyCode == GLFW.GLFW_KEY_UP) {
            this.cursorY = Math.clamp(this.cursorY - SENSITIVITY, this.getY(), this.getY() + this.height);
            this.triggerChange();
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_DOWN) {
            this.cursorY = Math.clamp(this.cursorY + SENSITIVITY, this.getY(), this.getY() + this.height);
            this.triggerChange();
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_LEFT) {
            this.cursorX = Math.clamp(this.cursorX - SENSITIVITY, this.getX(), this.getX() + this.width);
            this.triggerChange();
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_RIGHT) {
            this.cursorX = Math.clamp(this.cursorX + SENSITIVITY, this.getX(), this.getX() + this.width);
            this.triggerChange();
            return true;
        }
        return super.keyPressed(event);
    }

    public double getCursorY() {
        return this.cursorY;
    }

    public double getCursorX() {
        return this.cursorX;
    }

    public boolean isUsed() {
        return this.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard();
    }

    private void triggerChange() {
        this.mouseMovedCallback.mouseMoved(this.cursorX, this.cursorY);
    }

    private boolean triggerClick(@MagicConstant(flags = { PRIMARY_MOUSE_BUTTON, SECONDARY_MOUSE_BUTTON, MIDDLE_MOUSE_BUTTON }) int button) {
        return this.mouseClickedCallback.mouseClicked(new MouseButtonEvent(this.cursorX, this.cursorY, new MouseButtonInfo(button, 0)), false);
    }

    @FunctionalInterface
    public interface MouseMovedCallback {
        void mouseMoved(double mouseX, double mouseY);
    }

    @FunctionalInterface
    public interface MouseClickedCallback {
        boolean mouseClicked(MouseButtonEvent event, boolean doubleClick);
    }
}
