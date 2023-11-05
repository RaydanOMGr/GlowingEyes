package me.andreasmelone.glowingeyes.common.capability;

import java.awt.*;
import java.util.HashMap;

public interface IGlowingEyesCapability {
    HashMap<Point, Color> getGlowingEyesMap();
    void setGlowingEyesMap(HashMap<Point, Color> glowingEyesMap);
}