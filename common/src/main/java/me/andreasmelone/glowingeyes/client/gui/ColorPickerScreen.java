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
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ColorPickerScreen extends Screen {
    private static final int UI_WIDTH = TextureLocations.UI_BACKGROUND_BROAD_WIDTH;
    private static final int UI_HEIGHT = TextureLocations.UI_BACKGROUND_BROAD_HEIGHT;

    private static final int EDIT_BOX_X = UI_WIDTH - 70;
    private static final int EDIT_BOX_Y = 20;
    private static final int EDIT_BOX_SPACING = 30;

    private static final int COLOR_WHEEL_WIDTH = 100;
    private static final int COLOR_WHEEL_X = 20;

    private static final int BRIGHTNESS_SLIDER_X = 120;
    private static final int BRIGHTNESS_SLIDER_WIDTH = 25;

    private static final int COLOR_WIDGET_HEIGHT = 100;

    private static final int SELECTED_COLOR_X = UI_WIDTH - 40;
    private static final int SELECTED_COLOR_Y = UI_HEIGHT - 40;
    private static final int SELECTED_COLOR_WIDTH = 25;
    private static final int SELECTED_COLOR_HEIGHT = 25;

    private static final int EDIT_BOX_WIDTH = 60;
    private static final int EDIT_BOX_HEIGHT = 20;

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
        this.parent = null;
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
        if(this.parent != null) this.parent.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - UI_WIDTH) / 2;
        this.guiTop = (this.height - UI_HEIGHT) / 2;

        this.colorWheelX = this.guiLeft + COLOR_WHEEL_X;
        this.colorWheelY = (this.height / 2) - (COLOR_WIDGET_HEIGHT / 2);

        this.brightnessSliderX = this.colorWheelX + BRIGHTNESS_SLIDER_X;
        this.brightnessSliderY = this.colorWheelY;

        this.editBoxMap.clear();
        this.editBoxMap.put(ColorType.RED,
                this.createEditBox(this.guiLeft + EDIT_BOX_X, this.guiTop + EDIT_BOX_Y,  Component.empty()));

        this.editBoxMap.put(ColorType.GREEN,
                this.createEditBox(this.guiLeft + EDIT_BOX_X, this.guiTop + EDIT_BOX_Y + (EDIT_BOX_SPACING * this.editBoxMap.size()), Component.empty()));

        this.editBoxMap.put(ColorType.BLUE,
                this.createEditBox(this.guiLeft + EDIT_BOX_X, this.guiTop + EDIT_BOX_Y + (EDIT_BOX_SPACING * this.editBoxMap.size()), Component.empty()));

        this.editBoxMap.put(ColorType.HEX,
                this.createEditBox(this.guiLeft + EDIT_BOX_X, this.guiTop + EDIT_BOX_Y + (EDIT_BOX_SPACING * this.editBoxMap.size()), Component.empty()));

        this.editBoxMap.forEach((type, field) -> {
            field.setResponder((string) -> {
                if (!field.isFocused() || string.isEmpty()) return;
                this.editBoxMap.forEach((t, f) -> {
                    if(t != type) f.setFocused(false);
                });
                float[] hsb = ColorUtil.getHSBFromRGB(type.parseAndUpdate(this.mod.getModVariables().getFinalColor(), string).getRGB());
                this.changeColor(hsb[0], hsb[1], hsb[2], t -> t == type);
            });
            this.addRenderableWidget(field);
        });

        this.colorPickerWidget = this.createOrUpdateWidget(this.colorPickerWidget, this.colorWheelX, this.colorWheelY,() -> {
            ColorPickerWidget widget = new ColorPickerWidget(this.colorWheelX, this.colorWheelY, COLOR_WHEEL_WIDTH, COLOR_WIDGET_HEIGHT,
                    this.mod.getModVariables().getHue(), this.mod.getModVariables().getBrightness(), this.mod.getModVariables().getSaturation());
            widget.onChange((picker) -> {
                this.changeColor(this.mod.getModVariables().getHue(), picker.getSaturation(), picker.getBrightness());
            });
            return widget;
        });
        this.colorSliderWidget = this.createOrUpdateWidget(this.colorSliderWidget, this.brightnessSliderX, this.brightnessSliderY,() -> {
            ColorSliderWidget widget = new ColorSliderWidget(this.brightnessSliderX, this.brightnessSliderY, BRIGHTNESS_SLIDER_WIDTH, COLOR_WIDGET_HEIGHT,
                    this.mod.getModVariables().getHue());
            widget.onChange((slider) -> {
                this.changeColor(slider.getHue(), this.mod.getModVariables().getSaturation(), this.mod.getModVariables().getBrightness());
            });
            return widget;
        });

        this.addRenderableWidget(this.colorPickerWidget);
        this.addRenderableWidget(this.colorSliderWidget);
        this.changeColor(this.mod.getModVariables().getHue(), this.mod.getModVariables().getSaturation(), this.mod.getModVariables().getBrightness());
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        if(this.parent != null) {
            ctx.pose().pushPose();
            ctx.pose().translate(0, 0, -100);
            this.parent.render(ctx, 0, 0, delta);
            ctx.pose().popPose();
        }

        super.renderBackground(ctx, mouseX, mouseY, delta);
        GuiUtil.drawBackground(ctx,
                TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, UI_WIDTH, UI_HEIGHT);

        // draw the selected color on the right bottom
        ctx.fill(
                this.guiLeft + SELECTED_COLOR_X, this.guiTop + SELECTED_COLOR_Y,
                this.guiLeft + SELECTED_COLOR_X + SELECTED_COLOR_WIDTH, this.guiTop + SELECTED_COLOR_Y + SELECTED_COLOR_HEIGHT,
                this.mod.getModVariables().getFinalColor().getRGB()
        );

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if(this.parent != null) {
            Minecraft.getInstance().setScreen(this.parent);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTick) {
    }

    private void changeColor(float hue, float saturation, float brightness) {
        this.changeColor(hue, saturation, brightness, (type) -> false);
    }

    private void changeColor(float hue, float saturation, float brightness, Predicate<ColorType> predicate) {
        this.mod.getModVariables().setHue(hue);
        this.mod.getModVariables().setSaturation(saturation);
        this.mod.getModVariables().setBrightness(brightness);

        this.colorPickerWidget.setHue(hue);
        this.colorPickerWidget.setSaturation(saturation);
        this.colorPickerWidget.setBrightness(brightness);

        this.colorSliderWidget.setHue(hue);

        this.editBoxMap.forEach((type, box) -> {
            if(predicate.test(type)) return;
            box.setValue(type.get(this.mod.getModVariables().getFinalColor()));
        });
    }

    private EditBox createEditBox(int x, int y, Component component) {
        return new EditBox(
                this.font,
                x, y,
                EDIT_BOX_WIDTH, EDIT_BOX_HEIGHT,
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