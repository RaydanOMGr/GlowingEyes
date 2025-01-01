package me.andreasmelone.glowingeyes.client.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ToggleableImageButton extends Button {
    private long lastPressed = 0;

    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    private boolean toggledOn;

    public ToggleableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation, Button.OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, height, resourceLocation, 256, 256, onPress);
    }

    public ToggleableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, Button.OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, 256, 256, onPress);
    }

    public ToggleableImageButton(
            int x,
            int y,
            int width,
            int height,
            int xTexStart,
            int yTexStart,
            int yDiffTex,
            ResourceLocation resourceLocation,
            int textureWidth,
            int textureHeight,
            Button.OnPress onPress
    ) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, CommonComponents.EMPTY);
    }

    public ToggleableImageButton(
            int x,
            int y,
            int width,
            int height,
            int xTexStart,
            int yTexStart,
            int yDiffTex,
            ResourceLocation resourceLocation,
            int textureWidth,
            int textureHeight,
            Button.OnPress onPress,
            Component message
    ) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, NO_TOOLTIP, message);
    }

    public ToggleableImageButton(
            int x,
            int y,
            int width,
            int height,
            int xTexStart,
            int yTexStart,
            int yDiffTex,
            ResourceLocation resourceLocation,
            int textureWidth,
            int textureHeight,
            Button.OnPress onPress,
            Button.OnTooltip onTooltip,
            Component message
    ) {
        super(x, y, width, height, message, onPress, onTooltip);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = resourceLocation;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        int i = this.yTexStart;
        if (!this.isActive() || this.toggledOn) {
            i += this.yDiffTex * 2;
        } else if (this.isHoveredOrFocused()) {
            i += this.yDiffTex;
        }

        RenderSystem.enableDepthTest();
        blit(poseStack, this.x, this.y, (float)this.xTexStart, (float)i, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
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
