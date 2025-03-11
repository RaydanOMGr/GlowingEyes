package me.andreasmelone.glowingeyes.forge.common.component.eyes;

import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GlowingEyesImpl implements IGlowingEyes {
    private boolean toggledOn = true;
    private Map<Point, Color> glowingEyesMap = new HashMap<>();

    @NotNull
    @Override
    public Map<Point, Color> getGlowingEyesMap() {
        return this.glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMap(@NotNull Map<Point, Color> glowingEyesMap) {
        this.glowingEyesMap = glowingEyesMap;
    }

    @Override
    public boolean isToggledOn() {
        return toggledOn;
    }

    @Override
    public void setToggledOn(boolean toggledOn) {
        this.toggledOn = toggledOn;
    }
}
