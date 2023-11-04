package me.andreasmelone.glowingeyes.client.ui;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiButtonColorPicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

// uhh, I'm still trying to implement this, don't mind me
public class EyesEditScreen extends GuiScreen {
    private final int MENU_WIDTH = 212 * 2;
    private final int MENU_HEIGHT = 212;

    private int guiLeft;
    private int guiTop;

    protected int xSize = 176;
    protected int ySize = 166;

    private int middleX;
    private int middleY;

    private Minecraft mc;
    private BufferedImage bufferedImage;
    private HashMap<Point, Color> pixelMap = GlowingEyes.proxy.getPixelMap();

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
                new GuiButtonColorPicker(1, this.guiLeft + 170, this.guiTop + 140)
        );

        ResourceLocation location = Minecraft.getMinecraft().player.getLocationSkin();
        try {
            bufferedImage = TextureUtil.readBufferedImage(mc.getResourceManager().getResource(location).getInputStream());

            // cut out the player's face
            bufferedImage = bufferedImage.getSubimage(8, 8, 8, 8);
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

        // draw the menu background (just an opaque black square of size MENU_WIDTH x MENU_HEIGHT)
        drawRect(guiLeft + (xSize - MENU_WIDTH) / 2, guiTop + (ySize - MENU_HEIGHT) / 2,
                guiLeft + (xSize + MENU_WIDTH) / 2, guiTop + (ySize + MENU_HEIGHT) / 2, 0x99000000);

        drawCenteredString(fontRenderer, "Glowing Eyes Editor",
                middleX, guiTop - 15, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // when a pixel is clicked, change its colour to red
        int x = (mouseX - this.guiLeft - (this.xSize - (bufferedImage.getWidth() * 16 + (bufferedImage.getWidth() - 1) * 2)) / 2) / 18;
        int y = (mouseY - this.guiTop - (this.ySize - (bufferedImage.getHeight() * 16 + (bufferedImage.getHeight() - 1) * 2)) / 2) / 18;

        if(mouseButton == 0) {
            if (x >= 0 && x < bufferedImage.getWidth() && y >= 0 && y < bufferedImage.getHeight() && !pixelMap.containsKey(new Point(x, y))) {
                pixelMap.put(new Point(x, y), GlowingEyes.proxy.getPixelColor());
            }
        } else if(mouseButton == 1) {
            if (x >= 0 && x < bufferedImage.getWidth() && y >= 0 && y < bufferedImage.getHeight()) {
                pixelMap.remove(new Point(x, y));
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
        if (button.id == 1) {
            // open the color picker
            mc.displayGuiScreen(new ColorPickerScreen(this));
        }
    }
}
