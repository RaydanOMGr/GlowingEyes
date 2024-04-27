package me.andreasmelone.glowingeyes.client.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class BigSelectableButton extends Button {
    private boolean isSelected = false;

    public BigSelectableButton(int x, int y, Component buttonText, Button.OnPress pressedAction) {
        super(x, y, 128, 29, buttonText, pressedAction, Supplier::get);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
            RenderSystem.setShaderTexture(0, TextureLocations.BIG_BUTTON);
            boolean flag = mouseX >= this.getX() && mouseY >= this.getY()
                    && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            int i = 0;
            if(flag) {
                i += this.height;
            }
            if(isSelected) {
                i += this.height * 2;
            }


            blit(
                    poseStack,
                    this.getX(), this.getY(),
                    0, i,
                    128, 29,
                    128, 128
            );

            Minecraft mc = Minecraft.getInstance();
            mc.font.draw(
                    poseStack,
                    this.getMessage(),
                    this.getX() + (float) this.width / 2 - (float) mc.font.width(this.getMessage()) / 2,
                    this.getY() + (float) (this.height - 8) / 2,
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