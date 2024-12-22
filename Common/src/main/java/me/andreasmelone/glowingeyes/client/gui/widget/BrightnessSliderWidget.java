package me.andreasmelone.glowingeyes.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.client.util.color.ColorUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BrightnessSliderWidget extends AbstractWidget implements Widget, GuiEventListener {
    public int color;
    public float selectedBrightness;

    private final List<Consumer<BrightnessSliderWidget>> onChangeListeners = new ArrayList<>();

    public BrightnessSliderWidget(int x, int y, int width, int height, int color, float selectedBrightness) {
        super(x, y, width, height, Component.empty());
        this.color = color;
        this.setSelectedBrightness(selectedBrightness);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        int brightened = ColorUtil.getRGBFromBrightness(color, 1.0f);
        fillGradient(
                poseStack,
                x, y,
                x + width, y + height,
                brightened, Color.BLACK.getRGB()
        );

        RenderSystem.setShaderTexture(0, TextureLocations.BRIGHTNESS_CURSOR);
        blit(
                poseStack,
                x - (2 * (width / 16)), getCursor() - (4 * (width / 16)),
                width + (4 * (width / 16)), height / 3,
                0, 0,
                16, 16,
                16, 16
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isInbounds(new Point((int) mouseX, (int) mouseY))) return mouseDragged(mouseX, mouseY, button, 0, 0);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(mouseY >= y && mouseY <= y + height) this.setCursor((int) mouseY);
        triggerChange();

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public int getCursor() {
        return (int) (((this.selectedBrightness) * height) + y);
    }

    public void setCursor(int cursor) {
        this.selectedBrightness = ((float) (cursor - y) / height);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getSelectedBrightness() {
        return 1.0f - selectedBrightness;
    }

    public void setSelectedBrightness(float selectedBrightness) {
        this.selectedBrightness = selectedBrightness;
    }

    public void onChange(Consumer<BrightnessSliderWidget> listener) {
        this.onChangeListeners.add(listener);
    }

    private void triggerChange() {
        this.onChangeListeners.forEach((l) -> l.accept(this));
    }

    private boolean isInbounds(Point point) {
        return point.x >= x
                && point.x <= x + width
                && point.y >= y
                && point.y <= y + height;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }
}
