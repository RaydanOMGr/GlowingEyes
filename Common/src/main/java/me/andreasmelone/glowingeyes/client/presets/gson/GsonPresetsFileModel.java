package me.andreasmelone.glowingeyes.client.presets.gson;

import me.andreasmelone.glowingeyes.client.presets.Preset;

public class GsonPresetsFileModel {
    private Preset[] presets;

    public GsonPresetsFileModel() {
    }

    public GsonPresetsFileModel(Preset[] presets) {
        this.presets = presets;
    }

    public Preset[] getPresets() {
        return presets;
    }

    public void setPresets(Preset[] presets) {
        this.presets = presets;
    }
}