package me.andreasmelone.glowingeyes.client.presets;

import com.google.common.collect.ImmutableList;
import me.andreasmelone.glowingeyes.common.util.ModInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PresetManager {
    private List<Preset> presets = new ArrayList<>();

    public void loadPresets() {
        HashMap<Point, Color> content = new HashMap<>();
        content.put(new Point(3, 5), ModInfo.DEFAULT_EYE_COLOR);
        content.put(new Point(6, 5), ModInfo.DEFAULT_EYE_COLOR);
        presets.add(new Preset("Eyes 1", content));
    }

    public List<Preset> getPresets() {
        return ImmutableList.copyOf(this.presets);
    }
}
