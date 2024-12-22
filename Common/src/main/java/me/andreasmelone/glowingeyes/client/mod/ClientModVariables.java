package me.andreasmelone.glowingeyes.client.mod;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.color.ColorUtil;

import java.awt.*;

public class ClientModVariables {
    private Color selectedColor = GlowingEyes.DEFAULT_COLOR;
    private Color finalColor = GlowingEyes.DEFAULT_COLOR;
    private float brightness = (float) ColorUtil.getBrightnessFromRGB(selectedColor.getRGB()) / 100.0f;

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        this.finalColor = new Color(ColorUtil.getRGBFromBrightness(
                this.getSelectedColor().getRGB(),
                this.getBrightness()
        ));
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
        this.finalColor = new Color(ColorUtil.getRGBFromBrightness(
                this.getSelectedColor().getRGB(),
                this.getBrightness()
        ));
    }

    public Color getFinalColor() {
        return finalColor;
    }
}
