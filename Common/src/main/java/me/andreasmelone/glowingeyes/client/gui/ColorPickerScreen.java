package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.client.gui.widget.BrightnessSliderWidget;
import me.andreasmelone.glowingeyes.client.gui.widget.ColorWheelWidget;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.client.util.color.ColorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
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

    private ColorWheelWidget colorWheel = null;
    private BrightnessSliderWidget brightnessSlider = null;
    private Map<ColorType, EditBox> editBoxMap = new EnumMap<>(ColorType.class);

    private final Screen parent;
    private final ClientModContext mod;
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
                this.editBoxMap.forEach((t, f) -> { if(t != type) f.setFocus(false); });
                this.changeColor(type.parseAndUpdate(mod.getModVariables().getSelectedColor(), string),
                        mod.getModVariables().getBrightness(), t -> t == type);
            });
            this.addRenderableWidget(field);
        });

        colorWheel = createOrUpdateWidget(colorWheel, colorWheelX, colorWheelY, () -> {
            ColorWheelWidget widget = new ColorWheelWidget(colorWheelX, colorWheelY, 100, mod.getModVariables().getSelectedColor());
            widget.onChange(wheel -> {
                    this.changeColor(wheel.getSelectedColor(), mod.getModVariables().getBrightness());
                    brightnessSlider.setColor(wheel.getSelectedColor().getRGB());
            });
            return widget;
        });
        brightnessSlider = createOrUpdateWidget(brightnessSlider, brightnessSliderX, brightnessSliderY, () -> {
            BrightnessSliderWidget widget = new BrightnessSliderWidget(
                    brightnessSliderX, brightnessSliderY, 30, 100,
                    mod.getModVariables().getSelectedColor().getRGB(),
                    mod.getModVariables().getBrightness());
            widget.onChange(slider -> {
                this.changeColor(mod.getModVariables().getSelectedColor(), slider.getSelectedBrightness());
            });
            return widget;
        });

        this.addRenderableWidget(colorWheel);
        this.addRenderableWidget(brightnessSlider);
        this.changeColor(mod.getModVariables().getSelectedColor(), mod.getModVariables().getBrightness());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        if(parent != null) {
            parent.render(poseStack, mouseX, mouseY, delta);
        }

        this.renderBackground(poseStack);
        GuiUtil.drawBackground(poseStack,
                TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        // draw the selected color on the right bottom
        Gui.fill(
                poseStack,
                this.guiLeft + this.xSize - 40, this.guiTop + this.ySize - 40,
                this.guiLeft + this.xSize - 15, this.guiTop + this.ySize - 15,
                mod.getModVariables().getFinalColor().getRGB()
        );

        super.render(poseStack, mouseX, mouseY, delta);
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

    private void changeColor(Color color, float brightness) {
        changeColor(color, brightness, (type) -> false);
    }

    private void changeColor(Color color, float brightness, Predicate<ColorType> predicate) {
        mod.getModVariables().setSelectedColor(color);
        mod.getModVariables().setBrightness(brightness);
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
            widget.x = x;
            widget.y = y;
        }
        return widget;
    }

}