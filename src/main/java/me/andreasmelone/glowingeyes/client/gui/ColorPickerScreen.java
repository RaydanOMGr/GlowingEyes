package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ColorPickerScreen extends Screen {
    private int guiLeft, guiTop;
    private final int xSize = 200;
    private final int ySize = 143;

    private Screen parent;
    public ColorPickerScreen() {
        super(Component.empty());
        parent = null;
    }

    public ColorPickerScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    EditBox red;
    EditBox green;
    EditBox blue;

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        // add a text field
        this.addRenderableWidget(red = new EditBox(
                this.font,
                (this.guiLeft + this.xSize) - 50, this.guiTop + 20,
                35, 20,
                Component.literal(String.valueOf(Util.round((float) GlowingEyes.DEFAULT_COLOR.getRed() / 255, 2)))
        ));

        this.addRenderableWidget(green = new EditBox(
                this.font,
                (this.guiLeft + this.xSize) - 50, this.guiTop + 50,
                35, 20,
                Component.literal(String.valueOf(Util.round((float) GlowingEyes.DEFAULT_COLOR.getGreen() / 255, 2)))
        ));

        this.addRenderableWidget(blue = new EditBox(
                this.font,
                (this.guiLeft + this.xSize) - 50, this.guiTop + 80,
                35, 20,
                Component.literal(String.valueOf(Util.round((float) GlowingEyes.DEFAULT_COLOR.getBlue() / 255, 2)))
        ));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        if(parent != null) {
            parent.render(poseStack, mouseX, mouseY, delta);
        }

        this.renderBackground(poseStack);
        GuiUtil.drawBackground(poseStack,
                TextureLocations.UI_BACKGROUND_SLIM, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        // draw the selected color on the right bottom
        fill(
                poseStack,
                this.guiLeft + this.xSize - 50, this.guiTop + this.ySize - 50,
                this.guiLeft + this.xSize - 10, this.guiTop + this.ySize - 10,
                selectedColor
        );

        WheelRenderer.renderColorWheel(poseStack, this.guiLeft + 20, (this.height / 2) - (100 / 2));
        super.render(poseStack, mouseX, mouseY, delta);
    }

    private int selectedColor = GlowingEyes.DEFAULT_COLOR.getRGB();
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX >= this.guiLeft + 20 && mouseX <= this.guiLeft + 20 + 100 &&
                mouseY >= (this.height / 2) - (100 / 2) && mouseY <= (this.height / 2) - (100 / 2) + 100) {
            int x = (int) (mouseX - (this.guiLeft + 20));
            int y = (int) (mouseY - ((this.height / 2) - (100 / 2)));
            int color = WheelRenderer.getColorAt(x, y);
            selectedColor = color;
            Color c = new Color(color);
            red.setValue(String.valueOf(Util.round((float) c.getRed() / 255, 2)));
            green.setValue(String.valueOf(Util.round((float) c.getGreen() / 255, 2)));
            blue.setValue(String.valueOf(Util.round((float) c.getBlue() / 255, 2)));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // on keyboard button click
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_F12) {
            WheelRenderer.resetColorWheel();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if(parent != null) {
            getMinecraft().setScreen(parent);
        }
    }
}
