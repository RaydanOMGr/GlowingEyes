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
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    GuiButtonBrush brushButton;
    GuiButtonEraser eraserButton;
    GuiButtonFill fillButton;

    private Mode mode = Mode.BRUSH;

    private final Minecraft mc;
    private BufferedImage bufferedImage;
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

        ResourceLocation location = Minecraft.getMinecraft().player.getLocationSkin();
        try {
            bufferedImage = TextureUtil.readBufferedImage(mc.getResourceManager().getResource(location).getInputStream());
            int x = bufferedImage.getWidth() / 8;
            int y = bufferedImage.getHeight() / 8;

            // cut out the player's face
            bufferedImage = bufferedImage.getSubimage(x, y, x, y);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        // make an array of colours from the buffered image
        int[] pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(),
                bufferedImage.getHeight(), pixels, 0, bufferedImage.getWidth());

        // draw the player's face in the center of the screen and keep a little space around each pixel
        int spaceBetweenPixels = 2; // Adjust this value as needed
        int pixelSize = 16; // Adjust this value as needed

        // first draw the background
        int left = this.guiLeft + (this.xSize - (bufferedImage.getWidth() * pixelSize + (bufferedImage.getWidth() - 1) * spaceBetweenPixels)) / 2;
        int top = this.guiTop + (this.ySize - (bufferedImage.getHeight() * pixelSize + (bufferedImage.getHeight() - 1) * spaceBetweenPixels)) / 2;

        drawRect(left - spaceBetweenPixels,
                top - spaceBetweenPixels,
                left + bufferedImage.getWidth() * (pixelSize + spaceBetweenPixels),
                top + bufferedImage.getHeight() * (pixelSize + spaceBetweenPixels),
                0xFFA0A0A0
        );

        // then draw the pixels
        for (int i = 0; i < pixels.length; i++) {
            int x = i % bufferedImage.getWidth();
            int y = i / bufferedImage.getWidth();

            int leftPixel = this.guiLeft + (this.xSize - (bufferedImage.getWidth() * pixelSize + (bufferedImage.getWidth() - 1) * spaceBetweenPixels)) / 2 + x * (pixelSize + spaceBetweenPixels);
            int topPixel = this.guiTop + (this.ySize - (bufferedImage.getHeight() * pixelSize + (bufferedImage.getHeight() - 1) * spaceBetweenPixels)) / 2 + y * (pixelSize + spaceBetweenPixels);
            int right = leftPixel + pixelSize;
            int bottom = topPixel + pixelSize;

            int color;
            if(pixelMap.containsKey(new Point(x, y))) {
                Color c = pixelMap.get(new Point(x, y));
                color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 160).getRGB();

                drawRect(leftPixel, topPixel, right, bottom, pixels[i]);
            } else {
                color = pixels[i];
            }

            drawRect(leftPixel, topPixel, right, bottom, color);
        }

        drawCenteredString(fontRenderer, "Glowing Eyes Editor",
                middleX, guiTop + 8, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // when a pixel is clicked, change its colour to red
        int x = (mouseX - this.guiLeft - (this.xSize - (bufferedImage.getWidth() * 16 + (bufferedImage.getWidth() - 1) * 2)) / 2) / 18;
        int y = (mouseY - this.guiTop - (this.ySize - (bufferedImage.getHeight() * 16 + (bufferedImage.getHeight() - 1) * 2)) / 2) / 18;

        if(mode == Mode.BRUSH) {
            if(mouseButton == 0) {
                if (x >= 0 && x < bufferedImage.getWidth() && y >= 0 && y < bufferedImage.getHeight() && !pixelMap.containsKey(new Point(x, y))) {
                    pixelMap.put(new Point(x, y), GlowingEyes.proxy.getPixelColor());
                }
                if(x >= 0 && x < bufferedImage.getWidth() && y >= 0 && y < bufferedImage.getHeight() && pixelMap.containsKey(new Point(x, y))) {
                    pixelMap.replace(new Point(x, y), GlowingEyes.proxy.getPixelColor());
                }
            } else if(mouseButton == 1) {
                if (x >= 0 && x < bufferedImage.getWidth() && y >= 0 && y < bufferedImage.getHeight()) {
                    pixelMap.remove(new Point(x, y));
                }
            }
        } else if(mode == Mode.ERASER) {
            if (x >= 0 && x < bufferedImage.getWidth() && y >= 0 && y < bufferedImage.getHeight()) {
                pixelMap.remove(new Point(x, y));
            }
        } else if(mode == Mode.FILL) {
            if (x >= 0 && x < bufferedImage.getWidth() && y >= 0 && y < bufferedImage.getHeight()) {
                // get the color of the pixel that was clicked
                Color clickedPixelColor = new Color(bufferedImage.getRGB(x, y));

                // loop through all pixels on the BufferedImage and push the pixel to the map if it's not already there
                if(mouseButton == 0) {
                    for (int i = 0; i < bufferedImage.getWidth(); i++) {
                        for (int j = 0; j < bufferedImage.getHeight(); j++) {
                            Color currentPixelColor = new Color(bufferedImage.getRGB(i, j));
                            if(!currentPixelColor.equals(clickedPixelColor)) continue;
                            if (!pixelMap.containsKey(new Point(i, j))) {
                                pixelMap.put(new Point(i, j), GlowingEyes.proxy.getPixelColor());
                            }
                        }
                    }
                } else if(mouseButton == 1) {
                    for (int i = 0; i < bufferedImage.getWidth(); i++) {
                        for (int j = 0; j < bufferedImage.getHeight(); j++) {
                            Color currentPixelColor = new Color(bufferedImage.getRGB(i, j));
                            if(!currentPixelColor.equals(clickedPixelColor)) continue;
                            pixelMap.remove(new Point(i, j));
                        }
                    }
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
        GlowingEyes.logger.info("Button pressed: " + button.id);
        GlowingEyes.logger.info("Mode: " + mode);

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
        GlowingEyes.logger.info("Brush button selected: " + brushButton.isSelected());

        if(button.id == 2 && !eraserButton.isSelected()) {
            // change the mode to eraser
            mode = Mode.ERASER;
            brushButton.setSelected(false);
            eraserButton.setSelected(true);
            fillButton.setSelected(false);
        }
        GlowingEyes.logger.info("Eraser button selected: " + eraserButton.isSelected());

        if(button.id == 3 && !fillButton.isSelected()) {
            // change the mode to fill
            mode = Mode.FILL;
            brushButton.setSelected(false);
            eraserButton.setSelected(false);
            fillButton.setSelected(true);
        }
        GlowingEyes.logger.info("Fill button selected: " + fillButton.isSelected());
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
