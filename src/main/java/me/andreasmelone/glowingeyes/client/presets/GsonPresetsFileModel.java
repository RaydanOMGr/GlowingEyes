package me.andreasmelone.glowingeyes.client.presets;

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
