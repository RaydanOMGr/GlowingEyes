package me.andreasmelone.glowingeyes.client.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ToggleableImageButton extends Button {
    protected final WidgetSprites sprites;

    private long lastPressed = 0;
    private boolean toggledOn;

    public ToggleableImageButton(int x, int y, int width, int height, WidgetSprites sprites, Button.OnPress onPress) {
        this(x, y, width, height, sprites, onPress, CommonComponents.EMPTY);
    }

    public ToggleableImageButton(int x, int y, int width, int height, WidgetSprites sprites, Button.OnPress onPress, Component message) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.sprites = sprites;
    }

    public ToggleableImageButton(int width, int height, WidgetSprites sprites, Button.OnPress onPress, Component message) {
        this(0, 0, width, height, sprites, onPress, message);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation resourceLocation = this.sprites.get(this.isActive() && !this.isToggledOn(), this.isHoveredOrFocused());
        guiGraphics.blitSprite(resourceLocation, this.getX(), this.getY(), this.width, this.height);
    }

    @Override
    public void onPress() {
        if(System.currentTimeMillis() >= lastPressed + 120) {
            super.onPress();
            lastPressed = System.currentTimeMillis();
        }
    }

    public boolean isToggledOn() {
        return toggledOn;
    }

    public void setToggledOn(boolean toggledOn) {
        this.toggledOn = toggledOn;
    }
}
