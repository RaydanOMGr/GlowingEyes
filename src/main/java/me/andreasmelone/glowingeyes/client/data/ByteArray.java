package me.andreasmelone.glowingeyes.client.data;

import java.awt.*;
import java.util.HashMap;

public class ByteArray {
    public int length;
    public byte[] bytes;

    public ByteArray(int length, byte[] bytes) {
        this.length = length;
        this.bytes = bytes;
    }

    public static ByteArray fromHashMap(HashMap<Point, Color> map) {
        byte[] bytes = new byte[map.size() * 6];
        int i = 0;
        for(Point key : map.keySet()) {
            bytes[i] = (byte)key.x;
            bytes[i + 1] = (byte)key.y;
            bytes[i + 2] = (byte)map.get(key).getRed();
            bytes[i + 3] = (byte)map.get(key).getGreen();
            bytes[i + 4] = (byte)map.get(key).getBlue();
            bytes[i + 5] = (byte)map.get(key).getAlpha();
            i += 6;
        }
        return new ByteArray(map.size() * 6, bytes);
    }

    public static HashMap<Point, Color> toHashMap(ByteArray byteArray) {
        HashMap<Point, Color> map = new HashMap<>();
        for(int i = 0; i < byteArray.length; i += 6) {
            int x = byteArray.bytes[i];
            int y = byteArray.bytes[i + 1];
            int red = byteArray.bytes[i + 2];
            int green = byteArray.bytes[i + 3];
            int blue = byteArray.bytes[i + 4];
            int alpha = byteArray.bytes[i + 5];
            map.put(new Point(x, y), new Color(red, green, blue, alpha));
        }
        return map;
    }
}
