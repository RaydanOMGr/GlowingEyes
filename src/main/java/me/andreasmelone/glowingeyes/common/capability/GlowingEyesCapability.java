package me.andreasmelone.glowingeyes.common.capability;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class GlowingEyesCapability implements IGlowingEyesCapability {
    private HashMap<Point, Color> glowingEyesMap = new HashMap<>();

    @Override
    public HashMap<Point, Color> getGlowingEyesMap() {
        return this.glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMap(HashMap<Point, Color> glowingEyesMap) {
        this.glowingEyesMap = glowingEyesMap;
    }
}
