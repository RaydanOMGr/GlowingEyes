package me.andreasmelone.glowingeyes.client.ui.preset;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.presets.Preset;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class CreatePresetScreen extends GuiScreen {
    int xSize = 200;
    int ySize = 143;

    GuiScreen parent;
    Minecraft mc;

    public CreatePresetScreen(Minecraft mc, GuiScreen parent) {
        this.parent = parent;
        this.mc = mc;
    }

    int guiLeft;
    int guiTop;

    GuiTextField nameField;
    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        nameField = new GuiTextField(
                0, this.fontRenderer,
                this.guiLeft + 20, this.guiTop + 50,
                this.xSize - (20 * 2), 20
        );
        nameField.setFocused(true);

        // make a create and a cancel button
        this.buttonList.add(new GuiButton(
                0,
                this.guiLeft + 20, this.guiTop + 100,
                80 - 5, 20,
                I18n.format("gui.create")
        ));
        this.buttonList.add(new GuiButton(
                1,
                this.guiLeft + 100 + (5 * 2), this.guiTop + 100,
                80 - 5, 20,
                I18n.format("gui.cancel")
        ));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        parent.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();
        GuiUtil.drawBackground(TextureLocations.UI_BACKGROUND_SLIM, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        this.drawCenteredString(this.fontRenderer, I18n.format("gui.create.title"), this.width / 2, this.guiTop + 10, 0xFFFFFF);

        this.drawCenteredString(this.fontRenderer, I18n.format("gui.name"), this.width / 2, this.guiTop + 40, 0xFFFFFF);
        nameField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id == 0) {
            // Create
            PresetManager.getInstance().createPreset(nameField.getText(), GlowingEyes.proxy.getPixelMap());
            this.mc.displayGuiScreen(parent);
        } else if(button.id == 1) {
            // Cancel
            this.mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        nameField.updateCursorCounter();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        nameField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
