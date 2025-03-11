package me.andreasmelone.glowingeyes.client.gui;

import me.andreasmelone.glowingeyes.client.gui.widget.ColorPickerWidget;
import me.andreasmelone.glowingeyes.client.gui.widget.ColorSliderWidget;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.client.util.color.ColorType;
import me.andreasmelone.glowingeyes.client.util.color.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ColorPickerScreen extends Screen {
    private final int xSize = 256;
    private final int ySize = 222;
    private int guiLeft, guiTop;
    
    private int colorWheelX, colorWheelY;
    private int brightnessSliderX, brightnessSliderY;

    private ColorPickerWidget colorPickerWidget;
    private ColorSliderWidget colorSliderWidget;

    private final Screen parent;
    private final ClientModContext mod;
    private final Map<ColorType, EditBox> editBoxMap = new EnumMap<>(ColorType.class);
    public ColorPickerScreen(ClientModContext mod) {
        super(Component.empty());
        parent = null;
        this.mod = mod;
    }

    public ColorPickerScreen(ClientModContext mod, Screen parent) {
        super(Component.empty());
        this.parent = parent;
        this.mod = mod;
    }

    @Override
    protected void init() {
        super.init();
        if(parent != null) parent.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.colorWheelX = this.guiLeft + 20;
        this.colorWheelY = (this.height / 2) - (100 / 2);

        this.brightnessSliderX = this.colorWheelX + 120;
        this.brightnessSliderY = this.colorWheelY;

        this.editBoxMap.clear();
        this.editBoxMap.put(ColorType.RED,
                this.createEditBox(this.guiLeft + this.xSize - 70, this.guiTop + 20,  Component.empty()));

        this.editBoxMap.put(ColorType.GREEN,
                this.createEditBox(this.guiLeft + this.xSize - 70, this.guiTop + 50, Component.empty()));

        this.editBoxMap.put(ColorType.BLUE,
                this.createEditBox(this.guiLeft + this.xSize - 70, this.guiTop + 80, Component.empty()));

        this.editBoxMap.put(ColorType.HEX,
                this.createEditBox(this.guiLeft + this.xSize - 70, this.guiTop + 110, Component.empty()));

        this.editBoxMap.forEach((type, field) -> {
            field.setResponder((string) -> {
                if (!field.isFocused() || string.isEmpty()) return;
                this.editBoxMap.forEach((t, f) -> { if(t != type) f.setFocused(false); });
                float[] hsb = ColorUtil.getHSBFromRGB(type.parseAndUpdate(mod.getModVariables().getFinalColor(), string).getRGB());
                this.changeColor(hsb[0], hsb[1], hsb[2], t -> t == type);
            });
            this.addRenderableWidget(field);
        });

        colorPickerWidget = createOrUpdateWidget(colorPickerWidget, colorWheelX, colorWheelY,() -> {
            ColorPickerWidget widget = new ColorPickerWidget(colorWheelX, colorWheelY, 100, 100,
                    mod.getModVariables().getHue(), mod.getModVariables().getBrightness(), mod.getModVariables().getSaturation());
            widget.onChange((picker) -> {
                this.changeColor(mod.getModVariables().getHue(), picker.getSaturation(), picker.getBrightness());
            });
            return widget;
        });
        colorSliderWidget = createOrUpdateWidget(colorSliderWidget, brightnessSliderX, brightnessSliderY,() -> {
            ColorSliderWidget widget = new ColorSliderWidget(brightnessSliderX, brightnessSliderY, 30, 100,
                    mod.getModVariables().getHue());
            widget.onChange((slider) -> {
                this.changeColor(slider.getHue(), mod.getModVariables().getSaturation(), mod.getModVariables().getBrightness());
            });
            return widget;
        });

        this.addRenderableWidget(colorPickerWidget);
        this.addRenderableWidget(colorSliderWidget);
        this.changeColor(mod.getModVariables().getHue(), mod.getModVariables().getSaturation(), mod.getModVariables().getBrightness());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if(parent != null) {
            parent.render(guiGraphics, 0, 0, delta);
        }

        this.renderBackground(guiGraphics);
        GuiUtil.drawBackground(guiGraphics,
                TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        // draw the selected color on the right bottom
        guiGraphics.fill(
                this.guiLeft + this.xSize - 40, this.guiTop + this.ySize - 40,
                this.guiLeft + this.xSize - 15, this.guiTop + this.ySize - 15,
                mod.getModVariables().getFinalColor().getRGB()
        );

        super.render(guiGraphics, mouseX, mouseY, delta);
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

    private void changeColor(float hue, float saturation, float brightness) {
        changeColor(hue, saturation, brightness, (type) -> false);
    }

    private void changeColor(float hue, float saturation, float brightness, Predicate<ColorType> predicate) {
        mod.getModVariables().setHue(hue);
        mod.getModVariables().setSaturation(saturation);
        mod.getModVariables().setBrightness(brightness);

        this.colorPickerWidget.setHue(hue);
        this.colorPickerWidget.setSaturation(saturation);
        this.colorPickerWidget.setBrightness(brightness);

        this.colorSliderWidget.setHue(hue);

        editBoxMap.forEach((type, box) -> {
            if(predicate.test(type)) return;
            box.setValue(type.get(mod.getModVariables().getFinalColor()));
        });
    }

    private EditBox createEditBox(int x, int y, Component component) {
        return new EditBox(
                this.font,
                x, y,
                60, 20,
                component
        );
    }

    private <T extends AbstractWidget> T createOrUpdateWidget(T widget, int x, int y, Supplier<T> widgetFactory) {
        if (widget == null) {
            widget = widgetFactory.get();
        } else {
            widget.setX(x);
            widget.setY(y);
        }
        return widget;
    }

}