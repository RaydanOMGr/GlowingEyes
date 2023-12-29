package me.andreasmelone.glowingeyes.client.ui;

import me.andreasmelone.glowingeyes.client.presets.Preset;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiBigSelectableButton;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class PresetsScreen extends GuiScreen {
    private int guiLeft;
    private int guiTop;

    protected int xSize = 256; // the size of the texture is 221x222
    protected int ySize = 222; // the size of the texture is 221x222

    private final GuiScreen parent;
    private final PresetManager presetManager = PresetManager.getInstance();

    public PresetsScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        List<Preset> presets = presetManager.getPresets();
        int i = 0;
        for(Preset preset : presets) {
            this.buttonList.add(new GuiBigSelectableButton(i, this.guiLeft + 10, this.guiTop + 10 + (i * 30), preset.getName()));
            i++;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiUtil.drawBackground(TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button instanceof GuiBigSelectableButton) {
            GuiBigSelectableButton bigButton = (GuiBigSelectableButton) button;
            bigButton.setSelected(true);
            for(GuiButton guiButton : this.buttonList) {
                if(guiButton instanceof GuiBigSelectableButton && guiButton != bigButton) {
                    ((GuiBigSelectableButton) guiButton).setSelected(false);
                }
            }
        }
    }
}
