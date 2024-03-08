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
    public static Color getSelectedColor() {
        return new Color(selectedColor);
    }

    private int guiLeft, guiTop;
    private final int xSize = 200;
    private final int ySize = 143;

    private int colorWheelX, colorWheelY;

    private final Screen parent;
    public ColorPickerScreen() {
        super(Component.empty());
        parent = null;
    }

    public ColorPickerScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    EditBox red;
    EditBox green;
    EditBox blue;

    @Override
    protected void init() {
        super.init();
        if(parent != null) parent.init(getMinecraft(), getMinecraft().getWindow().getGuiScaledWidth(), getMinecraft().getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.colorWheelX = this.guiLeft + 20;
        this.colorWheelY = (this.height / 2) - (100 / 2);

        this.addEditBox(
                red = new EditBox(
                        this.font,
                        this.guiLeft + this.xSize - 50, this.guiTop + 20,
                        40, 20,
                        Component.literal(String.valueOf(Util.round(GuiUtil.getRedFromRGB(GlowingEyes.DEFAULT_COLOR.getRGB()) / 255)))
                )
        );
        this.addEditBox(
                green = new EditBox(
                        this.font,
                        this.guiLeft + this.xSize - 50, this.guiTop + 50,
                        40, 20,
                        Component.literal(String.valueOf(Util.round(GuiUtil.getGreenFromRGB(GlowingEyes.DEFAULT_COLOR.getRGB()) / 255)))
                )
        );
        this.addEditBox(
                blue = new EditBox(
                        this.font,
                        this.guiLeft + this.xSize - 50, this.guiTop + 80,
                        40, 20,
                        Component.literal(String.valueOf(Util.round(GuiUtil.getBlueFromRGB(GlowingEyes.DEFAULT_COLOR.getRGB()) / 255)))
                )
        );

        selectedX = WheelRenderer.getPointFromColor(selectedColor).x;
        selectedY = WheelRenderer.getPointFromColor(selectedColor).y;

        red.setValue(String.valueOf(Util.round(GuiUtil.getRedFromRGB(selectedColor) / 255, 2)));
        green.setValue(String.valueOf(Util.round(GuiUtil.getGreenFromRGB(selectedColor) / 255, 2)));
        blue.setValue(String.valueOf(Util.round(GuiUtil.getBlueFromRGB(selectedColor) / 255, 2)));
    }

    private void addEditBox(EditBox editBox) {
        editBox.setResponder(getColorResponder(editBox));
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

            if (editBox == red)
                redValue = value * 255;
            else if (editBox == green)
                greenValue = value * 255;
            else if (editBox == blue)
                blueValue = value * 255;

            selectedColor = new Color(
                    redValue / 255f,
                    greenValue / 255f,
                    blueValue / 255f
            ).getRGB();

            selectedX = WheelRenderer.getPointFromColor(selectedColor).x;
            selectedY = WheelRenderer.getPointFromColor(selectedColor).y;
        };
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        if(parent != null) {
            parent.render(poseStack, mouseX, mouseY, delta);
        }

        this.renderBackground(poseStack);
        GuiUtil.drawBackground(poseStack,
                TextureLocations.UI_BACKGROUND_SLIM, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        // draw the selected color on the right bottom
        fill(
                poseStack,
                this.guiLeft + this.xSize - 40, this.guiTop + this.ySize - 40,
                this.guiLeft + this.xSize - 15, this.guiTop + this.ySize - 15,
                selectedColor
        );

        WheelRenderer.renderColorWheel(poseStack, colorWheelX, colorWheelY);
        renderCursor(poseStack);

        super.render(poseStack, mouseX, mouseY, delta);
    }

    private void renderCursor(PoseStack poseStack) {
        // the x and y are relative to the color wheel, we need to get the position relative to this whole screen
        int relativeX = (int) (selectedX / WheelRenderer.SCALE) + colorWheelX;
        int relativeY = (int) (selectedY / WheelRenderer.SCALE) + colorWheelY;

        RenderSystem.setShaderTexture(0, TextureLocations.CURSOR);
        blit(
                poseStack,
                relativeX, relativeY,
                0, 0,
                8, 8,
                8, 8
        );
    }

    private int selectedX, selectedY;
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX >= colorWheelX && mouseX <= colorWheelX + 100 &&
                mouseY >= colorWheelY && mouseY <= colorWheelY + 100) {
            int x = (int) ((mouseX - colorWheelX) * WheelRenderer.SCALE);
            int y = (int) ((mouseY - colorWheelY) * WheelRenderer.SCALE);

            int c = WheelRenderer.getColorAt(x, y);
            if(c == 0) return super.mouseClicked(mouseX, mouseY, button);
            selectedColor = c;

            red.setValue(String.valueOf(Util.round(GuiUtil.getRedFromRGB(selectedColor) / 255, 2)));
            green.setValue(String.valueOf(Util.round(GuiUtil.getGreenFromRGB(selectedColor) / 255, 2)));
            blue.setValue(String.valueOf(Util.round(GuiUtil.getBlueFromRGB(selectedColor) / 255, 2)));

            selectedX = x;
            selectedY = y;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // on keyboard button click
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_F12) {
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
        if(parent != null) {
            getMinecraft().setScreen(parent);
            parent.init(
                    getMinecraft(),
                    getMinecraft().getWindow().getGuiScaledWidth(),
                    getMinecraft().getWindow().getGuiScaledHeight()
            );
        }
    }
}