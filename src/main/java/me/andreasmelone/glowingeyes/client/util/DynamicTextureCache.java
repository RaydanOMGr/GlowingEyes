package me.andreasmelone.glowingeyes.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DynamicTextureCache {
    private DynamicTextureCache() {
    }

    private static final Map<Map<Point, Color>, ResourceLocation> cache = new HashMap<>();

    public static ResourceLocation getTexture(Map<Point, Color> glowingEyesMap) {
        ResourceLocation texture = cache.get(glowingEyesMap);
        if (texture != null) {
            return texture;
        }

        texture = createTexture(glowingEyesMap);
        cache.put(glowingEyesMap, texture);
        return texture;
    }

    public static void clear() {
        for(ResourceLocation texture : cache.values()) {
            Minecraft.getInstance().getTextureManager().release(texture);
        }
        cache.clear();
    }

    private static ResourceLocation createTexture(Map<Point, Color> glowingEyesMap) {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        for (Map.Entry<Point, Color> entry : glowingEyesMap.entrySet()) {
            Point point = entry.getKey();
            Color color = entry.getValue();
            image.setRGB(point.x + 8, point.y + 8, color.getRGB());
        }

        NativeImage nativeImage = GuiUtil.toNativeImage(image);
        DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
        return Minecraft.getInstance().getTextureManager().register(GlowingEyes.MOD_ID + "_" + UUID.randomUUID(), dynamicTexture);
    }

    private static <K, V> boolean areEqual(Map<K, V> map1, Map<K, V> map2) {
        if (map1.size() != map2.size()) {
            return false;
        }

        for (Map.Entry<K, V> entry : map1.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if (!map2.containsKey(key) || !map2.get(key).equals(value)) {
                return false;
            }
        }

        return true;
    }
}
