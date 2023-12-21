package me.andreasmelone.glowingeyes.client.ui;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiButtonBrush;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiButtonColorPicker;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiButtonEraser;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiButtonFill;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.ClientCapabilityMessage;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

public class EyesEditScreen extends GuiScreen {
    private int guiLeft;
    private int guiTop;

    // me and the whole modded mc server have been tryna figure out what this means
    protected int xSize = 256; // the size of the texture is 221x222
    protected int ySize = 222; // the size of the texture is 221x222

    private int middleX;
    private int middleY;

    int headX;
    int headY;
    int endHeadX;
    int endHeadY;

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

        // define variables
        headX = this.guiLeft + (this.xSize - (headSize * pixelSize + (headSize - 1) * spaceBetweenPixels)) / 2;
        headY = this.guiTop + (this.ySize - (headSize * pixelSize + (headSize - 1) * spaceBetweenPixels)) / 2;
        endHeadX = headX + headSize * pixelSize + (headSize - 1) * spaceBetweenPixels;
        endHeadY = headY + headSize * pixelSize + (headSize - 1) * spaceBetweenPixels;

        // draw background
        drawRect(
                headX - spaceBetweenPixels, headY - spaceBetweenPixels,
                endHeadX + spaceBetweenPixels, endHeadY + spaceBetweenPixels,
                new Color(160, 160, 160, 255).getRGB() // why make a new color and stuff? Because it shows in the color in my IDE!
        );

        TextureManager textureManager = mc.getTextureManager();
        textureManager.bindTexture(mc.player.getLocationSkin());

        for (int y = 0; y < headSize; y++) {
            for (int x = 0; x < headSize; x++) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                drawScaledCustomSizeModalRect(
                        headX + x * pixelSize + x * spaceBetweenPixels,
                        headY + y * pixelSize + y * spaceBetweenPixels,
                        8f + x, 8f + y,
                        1, 1,
                        pixelSize, pixelSize,
                        64, 64
                );

                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }

        for(Point point : pixelMap.keySet()) {
            Color color = pixelMap.get(point);
            drawRect(
                    headX + point.x * pixelSize + point.x * spaceBetweenPixels,
                    headY + point.y * pixelSize + point.y * spaceBetweenPixels,
                    headX + point.x * pixelSize + point.x * spaceBetweenPixels + pixelSize,
                    headY + point.y * pixelSize + point.y * spaceBetweenPixels + pixelSize,
                    color.getRGB()
            );
        }

        drawCenteredString(fontRenderer, "Glowing Eyes Editor",
                middleX, guiTop + 8, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // when a pixel is clicked, change its colour to red
        // if mode equals to something, then check mouse position, and do stuff
        if(mouseX >= headX && mouseX <= endHeadX && mouseY >= headY && mouseY <= endHeadY) {
            // get the pixel position
            int spaceBetweenPixels = 2;
            int pixelSize = 16;
            int headSize = 8;

            int x = (mouseX - headX) / (pixelSize + spaceBetweenPixels);
            int y = (mouseY - headY) / (pixelSize + spaceBetweenPixels);

            // do stuff
            if(mode == Mode.BRUSH) {
                if(mouseButton == 0) {
                    // set the pixel to the selected color
                    if(pixelMap.containsKey(new Point(x, y))) {
                        pixelMap.replace(new Point(x, y), GlowingEyes.proxy.getPixelColor());
                    } else {
                        pixelMap.put(new Point(x, y), GlowingEyes.proxy.getPixelColor());
                    }
                } else if(mouseButton == 1) {
                    // remove the pixel
                    pixelMap.remove(new Point(x, y));
                }
            }

            if(mode == Mode.ERASER) {
                if(mouseButton == 0) {
                    // remove the pixel
                    pixelMap.remove(new Point(x, y));
                }
            }
        }

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
            IGlowingEyesCapability cap = mc.player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

            GlowingEyes.logger.info("Checking if cap is null");
            if(cap == null) return;
            GlowingEyes.logger.info("Cap is not null");
            cap.setGlowingEyesMap(pixelMap);

            GlowingEyes.logger.info(cap.getGlowingEyesMap().toString());

            NetworkHandler.sendToServer(new ClientCapabilityMessage(cap, mc.player));
        }
    }

    enum Mode {
        BRUSH, ERASER, FILL;
    }
}
