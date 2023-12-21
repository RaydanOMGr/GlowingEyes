package me.andreasmelone.glowingeyes.common.capability;

import java.awt.*;
import java.util.HashMap;

public interface IGlowingEyesCapability {
    HashMap<Point, Color> getGlowingEyesMap();
    byte[] getGlowingEyesMapBytes();
    void setGlowingEyesMap(HashMap<Point, Color> glowingEyesMap);
    void setGlowingEyesMapBytes(byte[] glowingEyesMapBytes);
}