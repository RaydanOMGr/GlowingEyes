package me.andreasmelone.glowingeyes.client.ui.buttons;

import me.andreasmelone.glowingeyes.client.presets.Preset;

public class GuiPresetButton extends GuiBigSelectableButton {
    private Preset preset;
    public GuiPresetButton(int buttonId, int x, int y, Preset preset) {
        super(buttonId, x, y, preset.getName());
        this.preset = preset;
    }

    public Preset getPreset() {
        return preset;
    }

    public void setPreset(Preset preset) {
        this.preset = preset;
        this.displayString = preset.getName();
    }
}
