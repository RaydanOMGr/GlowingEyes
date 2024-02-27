package me.andreasmelone.glowingeyes.common.capability.eyes;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;

public interface IGlowingEyes extends Serializable {
    @Nonnull
    HashMap<Point, Color> getGlowingEyesMap();
    void setGlowingEyesMap(@Nonnull HashMap<Point, Color> glowingEyesMap);
    boolean isToggledOn();
    void setToggledOn(boolean toggledOn);
}
