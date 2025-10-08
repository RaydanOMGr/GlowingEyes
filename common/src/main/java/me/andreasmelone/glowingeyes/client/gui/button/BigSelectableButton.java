package me.andreasmelone.glowingeyes.client.gui.button;

import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

public class BigSelectableButton extends Button {
    public static final int WIDTH = TextureLocations.BIG_BUTTON_WIDTH;
    public static final int HEIGHT = TextureLocations.BIG_BUTTON_HEIGHT;

    private static final int ACTIVE_COLOR = 0xFFFFFFFF;
    private static final int NOT_ACTIVE_COLOR = 0xFFA0A0A0;

    private boolean isSelected = false;

    public BigSelectableButton(int x, int y, Component buttonText, OnPress pressedAction) {
        this(x, y, buttonText, pressedAction, DEFAULT_NARRATION);
    }

    public BigSelectableButton(int x, int y, Component buttonText, OnPress pressedAction, CreateNarration createNarration) {
        super(x, y, WIDTH, HEIGHT, buttonText, pressedAction, createNarration);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
            Minecraft mc = Minecraft.getInstance();
            ResourceLocation sprite = TextureLocations.BIG_BUTTON.get(!this.isSelected() && this.isActive(), this.isHoveredOrFocused());
            ctx.blitSprite(
                    RenderType::guiTextured,
                    sprite,
                    this.getX(), this.getY(),
                    WIDTH, HEIGHT,
                    ARGB.white(this.alpha)
            );

            Color color = new Color(this.active ? ACTIVE_COLOR : NOT_ACTIVE_COLOR);
            this.renderString(ctx, mc.font, color.withAlpha(this.alpha).getRGB());
        }
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public void toggleSelected() {
        this.isSelected = !this.isSelected;
    }
}