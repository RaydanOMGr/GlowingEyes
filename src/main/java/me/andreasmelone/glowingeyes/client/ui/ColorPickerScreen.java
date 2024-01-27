package me.andreasmelone.glowingeyes.client.ui;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import java.awt.*;
import java.io.IOException;

public class ColorPickerScreen extends GuiScreen {
    private final boolean pauseGame;

    private int guiLeft;
    private int guiTop;

    protected int xSize = 176;
    protected int ySize = 222;

    GuiLabel redLabel;
    GuiLabel greenLabel;
    GuiLabel blueLabel;
    //GuiLabel alphaLabel;

    GuiTextField redField;
    GuiTextField greenField;
    GuiTextField blueField;
    //GuiTextField alphaField;

    private final GuiScreen parent;

    public ColorPickerScreen() {
        this.parent = null;
        this.pauseGame = false;
    }

    public ColorPickerScreen(GuiScreen parent) {
        this.parent = parent;
        this.pauseGame = false;
    }

    public ColorPickerScreen(GuiScreen parent, boolean pause) {
        this.parent = parent;
        this.pauseGame = pause;
    }

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        int width = 160;
        int height = 20;
        int buttonWidth = width + 2;
        int buttonHeight = height;

        int xDifference = 8;
        int yDifference = 15;

        int xButtonDifference = xDifference - 1;

        int defaultOffset = 5;
        int defaultButtonOffset = 25;

        int labels = 0;
        int fields = 0;
        int buttons = 0;

        // add text fields for red, green, blue and alpha and a button to confirm
        redLabel = new GuiLabel(fontRenderer, 0,
                this.guiLeft + xDifference,
                guiTop + defaultOffset + yDifference * labels + fields * height + buttons * ((buttonHeight / 2) + yDifference),                width, height,
                0xFF0000
        );
        redLabel.addLine(I18n.format("gui.red"));
        labels++;

        redField = new GuiTextField(0, this.fontRenderer,
                this.guiLeft + xDifference,
                guiTop + defaultOffset + yDifference * labels + fields * height + buttons * ((buttonHeight / 2) + yDifference),
                width, height
        );
        redField.setMaxStringLength(3);
        redField.setText(String.valueOf(GlowingEyes.proxy.getPixelColor().getRed()));
        fields++;

        greenLabel = new GuiLabel(fontRenderer, 1,
                this.guiLeft + xDifference,
                guiTop + defaultOffset + yDifference * labels + fields * height + buttons * ((buttonHeight / 2) + yDifference),                width, height,
                0x00FF00
        );
        greenLabel.addLine(I18n.format("gui.green"));
        labels++;

        greenField = new GuiTextField(1, this.fontRenderer,
                this.guiLeft + xDifference,
                guiTop + defaultOffset + yDifference * labels + fields * height + buttons * ((buttonHeight / 2) + yDifference),
                width, height
        );
        greenField.setMaxStringLength(3);
        greenField.setText(String.valueOf(GlowingEyes.proxy.getPixelColor().getGreen()));
        fields++;

        blueLabel = new GuiLabel(fontRenderer, 2,
                this.guiLeft + xDifference,
                guiTop + defaultOffset + yDifference * labels + fields * height + buttons * ((buttonHeight / 2) + yDifference),
                width, height,
                0x0000FF
        );
        blueLabel.addLine(I18n.format("gui.blue"));
        labels++;

        blueField = new GuiTextField(2, this.fontRenderer,
                this.guiLeft + xDifference,
                guiTop + defaultOffset + yDifference * labels + fields * height + buttons * ((buttonHeight / 2) + yDifference),
                width, height
        );
        blueField.setMaxStringLength(3);
        blueField.setText(String.valueOf(GlowingEyes.proxy.getPixelColor().getBlue()));
        fields++;

//        alphaLabel = new GuiLabel(fontRenderer, 3,
//                this.guiLeft + xDifference,
//                guiTop + defaultOffset + yDifference * labels + fields * height + buttons * ((buttonHeight / 2) + yDifference),
//                width, height,
//                new Color(145, 145, 145).getRGB()
//        );
//        alphaLabel.addLine(I18n.format("gui.alpha"));
//        labels++;
//
//        alphaField = new GuiTextField(3, this.fontRenderer,
//                this.guiLeft + xDifference,
//                guiTop + defaultOffset + yDifference * labels + fields * height + buttons * ((buttonHeight / 2) + yDifference),
//                width, height
//        );
//        alphaField.setMaxStringLength(3);
//        alphaField.setText(String.valueOf(GlowingEyes.proxy.getPixelColor().getAlpha()));
//        fields++;

        Keyboard.enableRepeatEvents(true);

        // Confirm button
        this.buttonList.add(new GuiButton(0,
                this.guiLeft + xButtonDifference,
                guiTop + defaultOffset + defaultButtonOffset + yDifference * labels + fields * height + buttons * ((buttonHeight / 2) + yDifference),
                buttonWidth, buttonHeight,
                I18n.format("gui.confirm")
        ));
        buttons++;

        // Reset button
        this.buttonList.add(new GuiButton(1,
                this.guiLeft + xButtonDifference,
                guiTop + defaultOffset + defaultButtonOffset + yDifference * labels + fields * height + buttons * ((buttonHeight / 2) + yDifference),
                buttonWidth, buttonHeight,
                I18n.format("gui.reset")
        ));
        buttons++;

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiUtil.drawBackground(TextureLocations.UI_BACKGROUND, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        Minecraft mc = Minecraft.getMinecraft();

        // draw the text fields
        redLabel.drawLabel(mc, mouseX, mouseY);
        redField.drawTextBox();

        greenLabel.drawLabel(mc, mouseX, mouseY);
        greenField.drawTextBox();

        blueLabel.drawLabel(mc, mouseX, mouseY);
        blueField.drawTextBox();

//        alphaLabel.drawLabel(mc, mouseX, mouseY);
//        alphaField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return pauseGame;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1) {
            this.mc.displayGuiScreen(this.parent);
        }

        // text fields
        redField.textboxKeyTyped(typedChar, keyCode);
        greenField.textboxKeyTyped(typedChar, keyCode);
        blueField.textboxKeyTyped(typedChar, keyCode);
//        alphaField.textboxKeyTyped(typedChar, keyCode);

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // text fields
        redField.mouseClicked(mouseX, mouseY, mouseButton);
        greenField.mouseClicked(mouseX, mouseY, mouseButton);
        blueField.mouseClicked(mouseX, mouseY, mouseButton);
//        alphaField.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            // confirm button
            // get the values from the text fields
            int red;
            int green;
            int blue;
            int alpha;

            try {
                red = Integer.parseInt(redField.getText());
            } catch (NumberFormatException e) {
                red = GlowingEyes.proxy.getPixelColor().getRed();
            }

            try {
                green = Integer.parseInt(greenField.getText());
            } catch (NumberFormatException e) {
                green = GlowingEyes.proxy.getPixelColor().getGreen();
            }

            try {
                blue = Integer.parseInt(blueField.getText());
            } catch (NumberFormatException e) {
                blue = GlowingEyes.proxy.getPixelColor().getBlue();
            }

//            try {
//                alpha = Integer.parseInt(alphaField.getText());
//            } catch (NumberFormatException e) {
//                alpha = GlowingEyes.proxy.getPixelColor().getAlpha();
//            }

            red = Math.max(0, Math.min(255, red));
            green = Math.max(0, Math.min(255, green));
            blue = Math.max(0, Math.min(255, blue));
//            alpha = Math.max(0, Math.min(255, alpha));

            // create a color object from the values
            Color color = new Color(red, green, blue /*, alpha*/);
            GlowingEyes.proxy.setPixelColor(color);

            // close the screen
            this.mc.displayGuiScreen(this.parent);
        } else if(button.id == 1) {
            // reset button
            GlowingEyes.proxy.setPixelColor(ModInfo.DEFAULT_EYE_COLOR);
            redField.setText(String.valueOf(GlowingEyes.proxy.getPixelColor().getRed()));
            greenField.setText(String.valueOf(GlowingEyes.proxy.getPixelColor().getGreen()));
            blueField.setText(String.valueOf(GlowingEyes.proxy.getPixelColor().getBlue()));
//            alphaField.setText(String.valueOf(GlowingEyes.proxy.getPixelColor().getAlpha()));
        }
    }
}
