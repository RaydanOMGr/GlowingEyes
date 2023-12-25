package me.andreasmelone.glowingeyes.client.presets;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class PresetManager {
    private List<Preset> presets = new ArrayList<Preset>();

    public void loadPresets() {}

    public List<Preset> getPresets() {
        return ImmutableList.copyOf(this.presets);
    }
}
