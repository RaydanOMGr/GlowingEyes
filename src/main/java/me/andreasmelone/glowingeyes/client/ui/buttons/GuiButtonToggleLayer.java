package me.andreasmelone.glowingeyes.client.ui.buttons;

import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonToggleLayer extends GuiButton {
    private boolean isSecondLayer = false;

    public GuiButtonToggleLayer(int buttonId, int x, int y) {
        super(buttonId, x, y, 20, 20, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(TextureLocations.BUTTONS);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean inMouseRange = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = 0;

            if (inMouseRange) {
                i += this.height;
            }
            if(this.isSecondLayer) {
                i += this.height * 2;
            }

            this.drawTexturedModalRect(this.x, this.y, 20, i, this.width, this.height);
        }
    }


    public void toggleLayer() {
        this.isSecondLayer = !this.isSecondLayer;
    }
}
