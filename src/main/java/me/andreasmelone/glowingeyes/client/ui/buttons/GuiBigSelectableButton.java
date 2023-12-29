package me.andreasmelone.glowingeyes.client.ui.buttons;

import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiBigSelectableButton extends GuiButton {
    private boolean isSelected = false;

    public GuiBigSelectableButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, 128, 29, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
            mc.getTextureManager().bindTexture(TextureLocations.BIG_BUTTON);
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            int i = 0;
            if(flag) {
                i += this.height;
            }
            if(isSelected) {
                i += this.height * 2;
            }


            drawModalRectWithCustomSizedTexture(
                    this.x, this.y,
                    0, i,
                    128, 29,
                    128, 128
            );

            this.drawString(
                    mc.fontRenderer,
                    this.displayString,
                    this.x + 10,
                    this.y + (this.height / 2) - (mc.fontRenderer.FONT_HEIGHT / 2),
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
