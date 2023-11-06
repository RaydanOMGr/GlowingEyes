package me.andreasmelone.glowingeyes.client.ui;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.data.ByteArray;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiButtonBrush;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiButtonColorPicker;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiButtonEraser;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiButtonFill;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.packets.ClientCapabilityMessage;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import scala.tools.nsc.transform.SpecializeTypes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

public class EyesEditScreen extends GuiScreen {
    private int guiLeft;
    private int guiTop;

    // me and the whole modded mc server have been tryna figure out what this means
    protected int xSize = 256; // the size of the texture is 221x222
    protected int ySize = 222; // the size of the texture is 221x222

    private int middleX;
    private int middleY;

    GuiButtonBrush brushButton;
    GuiButtonEraser eraserButton;
    GuiButtonFill fillButton;

    private Mode mode = Mode.BRUSH;

    private final Minecraft mc;
    private final HashMap<Point, Color> pixelMap = GlowingEyes.proxy.getPixelMap();

    public EyesEditScreen(Minecraft mc) {
        super();
        this.mc = mc;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.middleX = this.guiLeft + this.xSize / 2;
        this.middleY = this.guiTop + this.ySize / 2;

        // add the color picker button
        this.buttonList.add(
                new GuiButtonColorPicker(0, this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 30)
        );
        // add the brush button
        this.buttonList.add(
                brushButton = new GuiButtonBrush(1, this.guiLeft + 8, this.guiTop + 70)
        );
        brushButton.setSelected(true);

        // add the eraser button
        this.buttonList.add(
                eraserButton = new GuiButtonEraser(2, this.guiLeft + 8, this.guiTop + 95)
        );
        eraserButton.setSelected(false);

        // add the fill button
        this.buttonList.add(
                fillButton = new GuiButtonFill(3, this.guiLeft + 8, this.guiTop + 120)
        );
        fillButton.setSelected(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // here is da background boyzzzzz (I'm so fucking tired of this weird scaling bullshit)
        this.drawDefaultBackground();
        GuiUtil.drawBackground(TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        // draw the player's face in the center of the screen and keep a little space around each pixel
        int spaceBetweenPixels = 2;
        int pixelSize = 16;
        final int headSize = 8;

        int headX = this.guiLeft + (this.xSize - (headSize * pixelSize + (headSize - 1) * spaceBetweenPixels)) / 2;
        int headY = this.guiTop + (this.ySize - (headSize * pixelSize + (headSize - 1) * spaceBetweenPixels)) / 2;

        TextureManager textureManager = mc.getTextureManager();
        textureManager.bindTexture(mc.player.getLocationSkin());

        for (int y = 0; y < headSize; y++) {
            for (int x = 0; x < headSize; x++) {
                Gui.drawScaledCustomSizeModalRect(headX + x * pixelSize + x * spaceBetweenPixels, headY + y * pixelSize + y * spaceBetweenPixels, 8f + x, 8f + y, 1, 1, pixelSize, pixelSize, 64, 64);
            }
        }

        drawCenteredString(fontRenderer, "Glowing Eyes Editor",
                middleX, guiTop + 8, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // when a pixel is clicked, change its colour to red
        // if mode equals to something, then check mouse position, and do stuff

        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // on button pressed
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            // open the color picker
            mc.displayGuiScreen(new ColorPickerScreen(this, this.doesGuiPauseGame()));
        }

        if(button.id == 1 && !brushButton.isSelected()) {
            // change the mode to brush
            mode = Mode.BRUSH;
            brushButton.setSelected(true);
            eraserButton.setSelected(false);
            fillButton.setSelected(false);
        }

        if(button.id == 2 && !eraserButton.isSelected()) {
            // change the mode to eraser
            mode = Mode.ERASER;
            brushButton.setSelected(false);
            eraserButton.setSelected(true);
            fillButton.setSelected(false);
        }

        if(button.id == 3 && !fillButton.isSelected()) {
            // change the mode to fill
            mode = Mode.FILL;
            brushButton.setSelected(false);
            eraserButton.setSelected(false);
            fillButton.setSelected(true);
        }
    }

    @Override
    public void onGuiClosed() {
        if(GlowingEyes.serverHasMod) {
            NetworkHandler.sendToServer(new ClientCapabilityMessage());
        }
    }

    enum Mode {
        BRUSH, ERASER, FILL;
    }
}
