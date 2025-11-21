package me.andreasmelone.glowingeyes.common.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;

/**
 * An alternative to AWTs {@link Color} class. Using AWT may lead to problems in the future, so this
 * class was made as an alternative to it.
 */
public class Color implements Serializable, Cloneable {
    public static final Codec<Color> CODEC = Codec.INT.xmap(Color::new, Color::getRGB);

    public static final StreamCodec<ByteBuf, Color> STREAM_CODEC = ByteBufCodecs.INT.map(Color::new, Color::getRGB);

    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color LIGHT_GRAY = new Color(192, 192, 192);
    public static final Color GRAY = new Color(128, 128, 128);
    public static final Color DARK_GRAY = new Color(64, 64, 64);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color PINK = new Color(255, 175, 175);
    public static final Color ORANGE = new Color(255, 200, 0);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color MAGENTA = new Color(255, 0, 255);
    public static final Color CYAN = new Color(0, 255, 255);
    public static final Color BLUE = new Color(0, 0, 255);

    private final int value;
    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    @ConstructorProperties({"red", "green", "blue", "alpha"})
    public Color(int r, int g, int b, int a) {
        this.value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF));
                        testColorValueRange(r,g,b,a);
    }

    public Color(int rgb) {
        this.value = 0xff000000 | rgb;
    }

    public Color(int rgba, boolean hasalpha) {
        if (hasalpha) {
            this.value = rgba;
        } else {
            this.value = 0xff000000 | rgba;
        }
    }

    public Color(float r, float g, float b) {
        this((int) (r*255+0.5), (int)(g*255+0.5), (int)(b*255+0.5));
        testColorValueRange(r,g,b,1.0f);
    }

    public int getRed() {
        return (this.getRGB() >> 16) & 0xFF;
    }

    public int getGreen() {
        return (this.getRGB() >> 8) & 0xFF;
    }

    public int getBlue() {
        return (this.getRGB()) & 0xFF;
    }

    public int getAlpha() {
        return (this.getRGB() >> 24) & 0xff;
    }

    public int getRGB() {
        return this.value;
    }

    public Color withAlpha(float alpha) {
        return this.withAlpha((int)(alpha * 255));
    }

    public Color withAlpha(int alpha) {
        return new Color(this.getRed(), this.getGreen(), this.getBlue(), alpha);
    }

    public static int rgbaToArgb(int rgba) {
        int r = (rgba >> 24) & 0xFF;
        int g = (rgba >> 16) & 0xFF;
        int b = (rgba >> 8) & 0xFF;
        int a = rgba & 0xFF;

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static void testColorValueRange(int r, int g, int b, int a) {
        boolean rangeError = false;
        String badComponentString = "";

        if (a < 0 || a > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Alpha";
        }
        if (r < 0 || r > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Red";
        }
        if (g < 0 || g > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Green";
        }
        if (b < 0 || b > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Blue";
        }
        if (rangeError) {
            throw new IllegalArgumentException("Color parameter outside of expected range:" + badComponentString);
        }
    }

    private static void testColorValueRange(float r, float g, float b, float a) {
        boolean rangeError = false;
        String badComponentString = "";
        if (a < 0.0 || a > 1.0) {
            rangeError = true;
            badComponentString = badComponentString + " Alpha";
        }
        if (r < 0.0 || r > 1.0) {
            rangeError = true;
            badComponentString = badComponentString + " Red";
        }
        if (g < 0.0 || g > 1.0) {
            rangeError = true;
            badComponentString = badComponentString + " Green";
        }
        if (b < 0.0 || b > 1.0) {
            rangeError = true;
            badComponentString = badComponentString + " Blue";
        }
        if (rangeError) {
            throw new IllegalArgumentException("Color parameter outside of expected range:" + badComponentString);
        }
    }

    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
        float hue, saturation, brightness;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int cmax = Math.max(r, g);
        if (b > cmax) cmax = b;
        int cmin = Math.min(r, g);
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float)java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b);
    }

    public static Color getHSBColor(float h, float s, float b) {
        return new Color(HSBtoRGB(h, s, b));
    }
    

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return this.value == color.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    @Override
    public String toString() {
        return "Color{" +
                "red=" + this.getRed() +
                ", green=" + this.getGreen() +
                ", blue=" + this.getBlue() +
                '}';
    }

    @Override
    public Color clone() {
        try {
            return (Color) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
