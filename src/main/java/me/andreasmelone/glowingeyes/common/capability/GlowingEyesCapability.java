package me.andreasmelone.glowingeyes.common.capability;

import java.awt.*;
import java.util.HashMap;

public class GlowingEyesCapability implements IGlowingEyesCapability {
    private HashMap<Point, Color> glowingEyesMap = new HashMap<>();

    @Override
    public HashMap<Point, Color> getGlowingEyesMap() {
        return this.glowingEyesMap;
    }

    @Override
    public byte[] getGlowingEyesMapBytes() {
        byte[] data = new byte[this.glowingEyesMap.size() * 3];
        int i = 0;
        for (Point point : this.glowingEyesMap.keySet()) {
            data[i] = (byte) point.x;
            data[i + 1] = (byte) point.y;
            data[i + 2] = (byte) this.glowingEyesMap.get(point).getRGB();
            i += 3;
        }
        return data;
    }

    @Override
    public void setGlowingEyesMap(HashMap<Point, Color> glowingEyesMap) {
        this.glowingEyesMap = glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMapBytes(byte[] glowingEyesMapBytes) {
        HashMap<Point, Color> glowingEyesMap = new HashMap<>();
        for (int i = 0; i < glowingEyesMapBytes.length; i += 3) {
            Point point = new Point(glowingEyesMapBytes[i], glowingEyesMapBytes[i + 1]);
            Color color = new Color(glowingEyesMapBytes[i + 2]);
            glowingEyesMap.put(point, color);
        }
        this.glowingEyesMap = glowingEyesMap;
    }
}
