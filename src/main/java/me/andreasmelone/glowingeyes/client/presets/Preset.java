package me.andreasmelone.glowingeyes.client.presets;

import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Preset {
    private String name;
    private final int id;
    private final HashMap<Point, Color> content;

    public Preset(String name, int id, HashMap<Point, Color> content) {
        this.name = name;
        this.id = id;
        this.content = content;
    }

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

    public HashMap<Point, Color> getContent() {
        // return a copy so that the original content can't be modified
        return new HashMap<>(this.content);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }
}
