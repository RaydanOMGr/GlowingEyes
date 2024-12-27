package me.andreasmelone.glowingeyes.client.mod;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.color.ColorUtil;

import java.awt.*;

public class ClientModVariables {
    private float hue = (float) ColorUtil.getHueFromRGB(
            GlowingEyes.DEFAULT_COLOR.getRGB()) / 360.0f;
    private float saturation = (float) ColorUtil.getSaturationFromRGB(
            GlowingEyes.DEFAULT_COLOR.getRGB()) / 100.0f;
    private float brightness = (float) ColorUtil.getBrightnessFromRGB(
            GlowingEyes.DEFAULT_COLOR.getRGB()) / 100.0f;

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public Color getFinalColor() {
        return Color.getHSBColor(this.hue, this.saturation, this.brightness);
    }

    public void setFinalColor(Color color) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }
}
