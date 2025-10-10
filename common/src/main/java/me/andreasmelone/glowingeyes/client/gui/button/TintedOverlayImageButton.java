package me.andreasmelone.glowingeyes.client.gui.button;

import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class TintedOverlayImageButton extends ImageButton {
    protected IntSupplier colorSupplier = () -> 0xFFFFFFFF;

    protected final ResourceLocation overlaySprite;
    public TintedOverlayImageButton(int x, int y, int width, int height, WidgetSprites sprites, ResourceLocation overlaySprite, Button.OnPress onPress) {
        this(x, y, width, height, sprites, overlaySprite, onPress, CommonComponents.EMPTY);
    }

    public TintedOverlayImageButton(int x, int y, int width, int height, WidgetSprites sprites, ResourceLocation overlaySprite, Button.OnPress onPress, Component message) {
        super(x, y, width, height, sprites, onPress, message);
        this.overlaySprite = overlaySprite;
    }

    public TintedOverlayImageButton(int width, int height, WidgetSprites sprites, ResourceLocation overlaySprite, Button.OnPress onPress, Component message) {
        this(0, 0, width, height, sprites, overlaySprite, onPress, message);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTick) {
        ResourceLocation sprite = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
        ctx.blitSprite(
                RenderType::guiTextured, sprite,
                this.getX(), this.getY(),
                this.width, this.height
        );
        if(this.colorSupplier == null || this.overlaySprite == null) return;
        GuiUtil.blitTintedSprite(
                ctx,
                RenderType::guiTextured, this.overlaySprite,
                this.getX(), this.getY(),
                this.width, this.height,
                this.colorSupplier.getAsInt()
        );
    }

    public void setColorSupplier(IntSupplier colorSupplier) {
        this.colorSupplier = colorSupplier;
    }

    public void setColorSupplier(Supplier<Integer> colorSupplier) {
        this.colorSupplier = colorSupplier::get;
    }
}
