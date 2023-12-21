package me.andreasmelone.glowingeyes.common.capability;

import java.awt.*;
import java.util.HashMap;

public class GlowingEyesCapability implements IGlowingEyesCapability {
    private byte[] glowingEyesMap = new byte[0];

    @Override
    public HashMap<Point, Color> getGlowingEyesMap() {
        HashMap<Point, Color> glowingEyesMap = new HashMap<>();
        if(this.glowingEyesMap.length % 3 != 0) return glowingEyesMap;
        for(int i = 0; i < this.glowingEyesMap.length; i += 3) {
            int x = this.glowingEyesMap[i];
            int y = this.glowingEyesMap[i + 1];
            int rgb = this.glowingEyesMap[i + 2];
            glowingEyesMap.put(new Point(x, y), new Color(rgb));
        }
        return glowingEyesMap;
    }

    @Override
    public byte[] getGlowingEyesMapBytes() {
        return glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMap(HashMap<Point, Color> glowingEyesMap) {
        byte[] data = new byte[glowingEyesMap.size() * 3];
        int i = 0;
        for(Point point : glowingEyesMap.keySet()) {
            data[i] = (byte) point.x;
            data[i + 1] = (byte) point.y;
            data[i + 2] = (byte) glowingEyesMap.get(point).getRGB();
            i += 3;
        }
        this.glowingEyesMap = data;
    }

    @Override
    public void setGlowingEyesMapBytes(byte[] glowingEyesMapBytes) {
        this.glowingEyesMap = glowingEyesMapBytes;
    }
}
