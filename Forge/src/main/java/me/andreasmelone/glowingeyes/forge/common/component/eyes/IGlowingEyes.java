package me.andreasmelone.glowingeyes.forge.common.component.eyes;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;

public interface IGlowingEyes extends Serializable {
    HashMap<Point, Color> getGlowingEyesMap();
    void setGlowingEyesMap(HashMap<Point, Color> glowingEyesMap);
    boolean isToggledOn();
    void setToggledOn(boolean toggledOn);
}
