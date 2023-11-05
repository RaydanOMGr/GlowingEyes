package me.andreasmelone.glowingeyes.client.ui.buttons;

import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonEraser extends GuiButton {
    private boolean selected = false;

    public GuiButtonEraser(int buttonId, int x, int y) {
        super(buttonId, x, y, 20, 20, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(TextureLocations.BUTTONS);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = 100;

            if(this.selected) {
                i += this.height * 2;
            }
            if (!this.selected && flag) {
                i += this.height;
            }

            this.drawTexturedModalRect(this.x, this.y, 0, i, this.width, this.height);
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }
}
