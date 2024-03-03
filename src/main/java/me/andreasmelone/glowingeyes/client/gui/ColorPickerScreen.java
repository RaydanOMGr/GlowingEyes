package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Consumer;

public class ColorPickerScreen extends Screen {
    private static int selectedColor = GlowingEyes.DEFAULT_COLOR.getRGB();
    private static final int GUI_WIDTH = 200;
    private static final int GUI_HEIGHT = 143;
    private static final int COLOR_WHEEL_X = 20;
    private static final int COLOR_WHEEL_Y = 20;

    private final Screen parent;
    private EditBox red;
    private EditBox green;
    private EditBox blue;
    private int selectedX, selectedY;

    public ColorPickerScreen() {
        this(null);
    }

    public ColorPickerScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        if (parent != null) parent.init(getMinecraft(), getMinecraft().getWindow().getGuiScaledWidth(), getMinecraft().getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;

        this.colorWheelX = this.guiLeft + COLOR_WHEEL_X;
        this.colorWheelY = this.guiTop + COLOR_WHEEL_Y;

        this.addEditBox(red = createEditBox(20, 20, GuiUtil.getRedFromRGB(selectedColor)));
        this.addEditBox(green = createEditBox(20, 50, GuiUtil.getGreenFromRGB(selectedColor)));
        this.addEditBox(blue = createEditBox(20, 80, GuiUtil.getBlueFromRGB(selectedColor)));

        selectedX = WheelRenderer.getPointFromColor(selectedColor).x;
        selectedY = WheelRenderer.getPointFromColor(selectedColor).y;
        red.setValue(String.valueOf(Util.round(GuiUtil.getRedFromRGB(selectedColor) / 255, 2)));
        green.setValue(String.valueOf(Util.round(GuiUtil.getGreenFromRGB(selectedColor) / 255, 2)));
        blue.setValue(String.valueOf(Util.round(GuiUtil.getBlueFromRGB(selectedColor) / 255, 2)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        if (parent != null) parent.render(poseStack, mouseX, mouseY, delta);

        this.renderBackground(poseStack);
        GuiUtil.drawBackground(poseStack, TextureLocations.UI_BACKGROUND_SLIM, this.guiLeft, this.guiTop, GUI_WIDTH, GUI_HEIGHT);

        fill(poseStack, this.guiLeft + GUI_WIDTH - 40, this.guiTop + GUI_HEIGHT - 40, this.guiLeft + GUI_WIDTH - 15, this.guiTop + GUI_HEIGHT - 15, selectedColor);

        WheelRenderer.renderColorWheel(poseStack, colorWheelX, colorWheelY);
        renderCursor(poseStack);

        super.render(poseStack, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= colorWheelX && mouseX <= colorWheelX + 100 &&
                mouseY >= colorWheelY && mouseY <= colorWheelY + 100) {
            int x = (int) ((mouseX - colorWheelX) * WheelRenderer.SCALE);
            int y = (int) ((mouseY - colorWheelY) * WheelRenderer.SCALE);
            int c = WheelRenderer.getColorAt(x, y);
            if (c == 0) return super.mouseClicked(mouseX, mouseY, button);
            selectedColor = c;
            red.setValue(String.valueOf(Util.round(GuiUtil.getRedFromRGB(selectedColor) / 255, 2)));
            green.setValue(String.valueOf(Util.round(GuiUtil.getGreenFromRGB(selectedColor) / 255, 2)));
            blue.setValue(String.valueOf(Util.round(GuiUtil.getBlueFromRGB(selectedColor) / 255, 2)));
            selectedX = x;
            selectedY = y;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_F12) {
            WheelRenderer.resetColorWheel();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (parent != null) {
            getMinecraft().setScreen(parent);
            parent.init(getMinecraft(), getMinecraft().getWindow().getGuiScaledWidth(), getMinecraft().getWindow().getGuiScaledHeight());
        }
    }

    private EditBox createEditBox(int x, int y, int value) {
        EditBox editBox = new EditBox(this.font, this.guiLeft + GUI_WIDTH - 50, this.guiTop + y, 40, 20, Component.literal(String.valueOf(Util.round(value / 255))));
        editBox.setResponder(getColorResponder(editBox));
        return editBox;
    }

    private void addEditBox(EditBox editBox) {
        this.addRenderableWidget(editBox);
    }

    private Consumer<String> getColorResponder(EditBox editBox) {
        return (s) -> {
            if (!editBox.isFocused() || s.isEmpty()) return;
            float value;
            try {
                value = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return;
            }
            if (value > 1) value = 1;
            if (value < 0) value = 0;
            float redValue = GuiUtil.getRedFromRGB(selectedColor);
            float greenValue = GuiUtil.getGreenFromRGB(selectedColor);
            float blueValue = GuiUtil.getBlueFromRGB(selectedColor);
            if (editBox == red) redValue = value * 255;
            else if (editBox == green) greenValue = value * 255;
            else if (editBox == blue) blueValue = value * 255;
            selectedColor = new Color(redValue / 255f, greenValue / 255f, blueValue / 255f).getRGB();
            selectedX = WheelRenderer.getPointFromColor(selectedColor).x;
            selectedY = WheelRenderer.getPointFromColor(selectedColor).y;
        };
    }

    private void renderCursor(PoseStack poseStack) {
        int relativeX = (int) (selectedX / WheelRenderer.SCALE) + colorWheelX;
        int relativeY = (int) (selectedY / WheelRenderer.SCALE) + colorWheelY;
        RenderSystem.setShaderTexture(0, TextureLocations.CURSOR);
        blit(poseStack, relativeX, relativeY, 0, 0, 8, 8, 8, 8);
    }
}
