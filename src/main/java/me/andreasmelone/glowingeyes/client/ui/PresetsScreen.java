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

    protected int xSize = 256; // the size of the texture is 256x222
    protected int ySize = 222; // the size of the texture is 256x222

    private final GuiScreen parent;
    private final PresetManager presetManager = PresetManager.getInstance();

    public PresetsScreen(GuiScreen parent) {
        this.parent = parent;
    }

    int page = 0;
    int buttons = 0;

    int selectedPreset = -1;

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        // <- and ->
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        this.guiLeft + 20, this.guiTop + 190,
                        20, 20,
                        "<"
                )
        );
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        this.guiLeft + 90, this.guiTop + 190,
                        20, 20,
                        ">"
                )
        );

        List<Preset> presets = presetManager.getPresets();
        int j = 0;
        for(Preset preset : presets) {
            if(j >= 6) break; // TODO: add scrolling
            this.buttonList.add(
                    new GuiBigSelectableButton(
                            j + buttons,
                            this.guiLeft + 10, this.guiTop + 10 + (j * 30),
                            preset.getName()
                    )
            );
            j++;
        }
        buttons += j;
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
        if(button.id == 0) {
            if(page > 0) {
                page--;
            }
            for(GuiButton guiButton : this.buttonList) {
                if(guiButton instanceof GuiBigSelectableButton) {
                    GuiBigSelectableButton bigButton = (GuiBigSelectableButton) guiButton;
                    Preset preset = presetManager.getPreset(page * 6 + bigButton.id - 2);
                    if(preset != null) {
                        bigButton.visible = true;
                        bigButton.displayString = preset.getName();
                    } else {
                        bigButton.visible = false;
                    }
                }
            }
        } else if(button.id == 1) {
            if(page < presetManager.getPresets().size() / 6) {
                page++;
            }
            for(GuiButton guiButton : this.buttonList) {
                if(guiButton instanceof GuiBigSelectableButton) {
                    GuiBigSelectableButton bigButton = (GuiBigSelectableButton) guiButton;
                    Preset preset = presetManager.getPreset(page * 6 + bigButton.id - 2);
                    if(preset != null) {
                        bigButton.visible = true;
                        bigButton.displayString = preset.getName();
                    } else {
                        bigButton.visible = false;
                    }
                }
            }
        }
    }
}
