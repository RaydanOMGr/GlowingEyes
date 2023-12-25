package me.andreasmelone.glowingeyes.common.capability.eyes;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;

public class GlowingEyesCapability implements IGlowingEyesCapability {
    private boolean toggledOn = false;
    private HashMap<Point, Color> glowingEyesMap = new HashMap<>();

    @Nonnull
    @Override
    public HashMap<Point, Color> getGlowingEyesMap() {
        return this.glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMap(@Nonnull HashMap<Point, Color> glowingEyesMap) {
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
