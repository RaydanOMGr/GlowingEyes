package me.andreasmelone.glowingeyes.client.gui.button;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BigSelectableButton extends Button {
    protected final WidgetSprites sprites = GuiUtil.createSprites(
            GlowingEyes.MOD_ID,
            "big/big_button",
            "big/big_button_disabled",
            "big/big_button_highlighted",
            "big/big_button_highlighted_disabled"
    );

    private boolean isSelected = false;

    public BigSelectableButton(int x, int y, Component buttonText, OnPress pressedAction) {
        this(x, y, buttonText, pressedAction, DEFAULT_NARRATION);
    }

    public BigSelectableButton(int x, int y, Component buttonText, OnPress pressedAction, CreateNarration createNarration) {
        super(x, y, 128, 29, buttonText, pressedAction, createNarration);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
            ResourceLocation sprite = this.sprites.get(!this.isSelected() && this.isActive(), this.isHoveredOrFocused());
            guiGraphics.blitSprite(
                    sprite,
                    this.getX(), this.getY(),
                    128, 29
            );

            Minecraft mc = Minecraft.getInstance();
            guiGraphics.drawString(
                    mc.font,
                    this.getMessage(),
                    (int)(this.getX() + (float) this.width / 2 - (float) mc.font.width(this.getMessage()) / 2),
                    (int)(this.getY() + (float) (this.height - 8) / 2),
                    0xFFFFFF
            );
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void toggleSelected() {
        isSelected = !isSelected;
    }
}