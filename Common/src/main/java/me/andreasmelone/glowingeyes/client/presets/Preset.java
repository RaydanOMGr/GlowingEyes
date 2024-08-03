package me.andreasmelone.glowingeyes.client.presets;

import com.mojang.blaze3d.platform.NativeImage;
import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.HashMap;

public class Preset {
    private String name;
    private final ResourceLocation id;
    private final HashMap<Point, Color> content;

    public Preset(String name, ResourceLocation id, HashMap<Point, Color> content) {
        this.name = name;
        this.id = id;
        this.content = content;
    }

    private DynamicTexture createDynamicTexture() {
        NativeImage eyeOverlayTexture = new NativeImage(64, 64, true);
        for (Point point : content.keySet()) {
            Color color = content.get(point);
            eyeOverlayTexture.setPixelRGBA(point.x + 8, point.y + 8, new Color(color.getBlue(), color.getGreen(), color.getRed(), color.getAlpha()).getRGB());
        }
        return new DynamicTexture(eyeOverlayTexture);
    }

    public ResourceLocation getResourceLocation() {
        return Minecraft.getInstance().getTextureManager().register(
                GlowingEyes.MOD_ID + ".eye_overlay_" + id,
                createDynamicTexture()
        );
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

    public ResourceLocation getId() {
        return this.id;
    }
}