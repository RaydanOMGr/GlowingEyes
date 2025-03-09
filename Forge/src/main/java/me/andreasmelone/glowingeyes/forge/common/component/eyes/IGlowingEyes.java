package me.andreasmelone.glowingeyes.forge.common.component.eyes;

import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;

import java.io.Serializable;
import java.util.Map;

public interface IGlowingEyes extends Serializable {
    Map<Point, Color> getGlowingEyesMap();
    void setGlowingEyesMap(Map<Point, Color> glowingEyesMap);
    boolean isToggledOn();
    void setToggledOn(boolean toggledOn);
}
