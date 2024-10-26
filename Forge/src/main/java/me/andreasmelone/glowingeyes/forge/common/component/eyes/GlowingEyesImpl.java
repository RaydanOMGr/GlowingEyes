package me.andreasmelone.glowingeyes.forge.common.component.eyes;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GlowingEyesImpl implements IGlowingEyes {
    private boolean toggledOn = true;
    private Map<Point, Color> glowingEyesMap = new HashMap<>();

    @Nonnull
    @Override
    public Map<Point, Color> getGlowingEyesMap() {
        return this.glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMap(@Nonnull Map<Point, Color> glowingEyesMap) {
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
