package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.ColorUtil;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.server.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.function.Consumer;

public class ColorPickerScreen extends Screen {
    private static int selectedColor = GlowingEyes.DEFAULT_COLOR.getRGB();

    private int guiLeft, guiTop;
    private final int xSize = 252;
    private final int ySize = 143;

    private int colorWheelX, colorWheelY;
    private int brightnessSliderX, brightnessSliderY;
    private int selectedX, selectedY;

    EditBox red, green, blue;

    private final Screen parent;
    public ColorPickerScreen() {
        super(Component.empty());
        parent = null;
    }

    public ColorPickerScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }


    @Override
    protected void init() {
        super.init();
        if(parent != null) parent.init(Minecraft.getInstance(), Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.colorWheelX = this.guiLeft + 20;
        this.colorWheelY = (this.height / 2) - (100 / 2);

        this.brightnessSliderX = this.colorWheelX + 120;
        this.brightnessSliderY = this.colorWheelY;

        this.addEditBox(
                red = new EditBox(
                        this.font,
                        this.guiLeft + this.xSize - 50, this.guiTop + 20,
                        40, 20,
                        Component.empty() // the label should stay uninitialized for now
                )
        );
        this.addEditBox(
                green = new EditBox(
                        this.font,
                        this.guiLeft + this.xSize - 50, this.guiTop + 50,
                        40, 20,
                        Component.empty()
                )
        );
        this.addEditBox(
                blue = new EditBox(
                        this.font,
                        this.guiLeft + this.xSize - 50, this.guiTop + 80,
                        40, 20,
                        Component.empty()
                )
        );

        selectedX = WheelRenderer.getPointFromColor(selectedColor).x;
        selectedY = WheelRenderer.getPointFromColor(selectedColor).y;

        red.setValue(String.valueOf(Util.round((float) ColorUtil.getRedFromRGB(selectedColor) / 255, 2)));
        green.setValue(String.valueOf(Util.round((float) ColorUtil.getGreenFromRGB(selectedColor) / 255, 2)));
        blue.setValue(String.valueOf(Util.round((float) ColorUtil.getBlueFromRGB(selectedColor) / 255, 2)));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if(parent != null) {
            parent.render(guiGraphics, mouseX, mouseY, delta);
        }

        this.renderBackground(guiGraphics);
        GuiUtil.drawBackground(guiGraphics,
                TextureLocations.UI_BACKGROUND_SLIM_LONG, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        // draw the selected color on the right bottom
        guiGraphics.fill(
                this.guiLeft + this.xSize - 40, this.guiTop + this.ySize - 40,
                this.guiLeft + this.xSize - 15, this.guiTop + this.ySize - 15,
                selectedColor
        );

        WheelRenderer.renderColorWheel(guiGraphics, colorWheelX, colorWheelY);
        renderCursor(guiGraphics);

        int selectedColorBrightened = ColorUtil.getRGBFromBrightness(selectedColor, 1.0f);
        guiGraphics.fillGradient(
                brightnessSliderX, brightnessSliderY,
                brightnessSliderX + 30, brightnessSliderY + 100,
                selectedColorBrightened, Color.BLACK.getRGB()
        );
        renderBrightnessCursor(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        boolean isColorWheelClicked = mouseX >= colorWheelX && mouseX <= colorWheelX + 100 &&
                mouseY >= colorWheelY && mouseY <= colorWheelY + 100;
        boolean isBrightnessSliderClicked = mouseX >= brightnessSliderX && mouseX <= brightnessSliderX + 30 &&
                mouseY >= brightnessSliderY && mouseY <= brightnessSliderY + 100 - 2;

        if(isColorWheelClicked) {
            colorWheelClicked(mouseX, mouseY);
        }
        if(isBrightnessSliderClicked) {
            brightnessSliderClicked(mouseX, mouseY);
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if(parent != null) {
            Minecraft.getInstance().setScreen(parent);
            parent.init(
                    Minecraft.getInstance(),
                    Minecraft.getInstance().getWindow().getGuiScaledWidth(),
                    Minecraft.getInstance().getWindow().getGuiScaledHeight()
            );
        }
    }

    private void colorWheelClicked(double mouseX, double mouseY) {
        int x = (int) ((mouseX - colorWheelX) * WheelRenderer.SCALE);
        int y = (int) ((mouseY - colorWheelY) * WheelRenderer.SCALE);

        int c = WheelRenderer.getColorAt(x, y);
        if (c == 0) return;
        selectedColor = c;

        red.setValue(String.valueOf(Util.round((float) ColorUtil.getRedFromRGB(selectedColor) / 255, 2)));
        green.setValue(String.valueOf(Util.round((float) ColorUtil.getGreenFromRGB(selectedColor) / 255, 2)));
        blue.setValue(String.valueOf(Util.round((float) ColorUtil.getBlueFromRGB(selectedColor) / 255, 2)));

        selectedX = x;
        selectedY = y;
    }

    private void brightnessSliderClicked(double mouseX, double mouseY) {
        int y = (int) (mouseY - brightnessSliderY);
        WheelRenderer.brightness = 1 - (y / 100f);
        if (WheelRenderer.brightness > 1.0f) WheelRenderer.brightness = 1.0f;
        if (WheelRenderer.brightness < 0.0f) WheelRenderer.brightness = 0.0f;
        selectedColor = ColorUtil.getRGBFromBrightness(selectedColor, (int) (WheelRenderer.brightness * 255));

        red.setValue(String.valueOf(Util.round((float) ColorUtil.getRedFromRGB(selectedColor) / 255, 2)));
        green.setValue(String.valueOf(Util.round((float) ColorUtil.getGreenFromRGB(selectedColor) / 255, 2)));
        blue.setValue(String.valueOf(Util.round((float) ColorUtil.getBlueFromRGB(selectedColor) / 255, 2)));
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

            float redValue = ColorUtil.getRedFromRGB(selectedColor);
            float greenValue = ColorUtil.getGreenFromRGB(selectedColor);
            float blueValue = ColorUtil.getBlueFromRGB(selectedColor);

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

            // change the brightness to the current brightness
            WheelRenderer.brightness = ColorUtil.getHSBFromRGB(selectedColor)[2];

            selectedX = WheelRenderer.getPointFromColor(selectedColor).x;
            selectedY = WheelRenderer.getPointFromColor(selectedColor).y;
        };
    }

    private void renderCursor(GuiGraphics guiGraphics) {
        // the x and y are relative to the color wheel, we need to get the position relative to this whole screen
        int relativeX = (int) (selectedX / WheelRenderer.SCALE) + colorWheelX;
        int relativeY = (int) (selectedY / WheelRenderer.SCALE) + colorWheelY;

        guiGraphics.blit(
                TextureLocations.CURSOR,
                relativeX - 3, relativeY - 3,
                0, 0,
                8, 8,
                8, 8
        );
    }

    private void renderBrightnessCursor(GuiGraphics guiGraphics) {
        int relativeY = (int) (brightnessSliderY + (1 - WheelRenderer.brightness) * 100);

        guiGraphics.blit(
                TextureLocations.BRIGHTNESS_CURSOR,
                colorWheelX + 118, relativeY - 1,
                34,4,
                0, 0,
                16, 3,
                16, 16
        );
    }

    public static Color getSelectedColor() {
        return new Color(selectedColor);
    }
    public static void setSelectedColor(Color color) {
        selectedColor = color.getRGB();
    }
}