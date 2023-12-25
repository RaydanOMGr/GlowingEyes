package me.andreasmelone.glowingeyes.client.presets;

import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Preset {
    public String name;
    public HashMap<Point, Color> content;

    private DynamicTexture createDynamicTexture() {
        BufferedImage eyeOverlayTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        for (Point point : content.keySet()) {
            Color color = content.get(point);
            eyeOverlayTexture.setRGB(point.x + 8, point.y + 8, color.getRGB());
        }
        return new DynamicTexture(eyeOverlayTexture);
    }

    public ResourceLocation getResourceLocation() {
        return Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation(ModInfo.MODID, createDynamicTexture());
    }
}
