package me.andreasmelone.glowingeyes.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DynamicTextureCache {
    private DynamicTextureCache() {
    }

    private static final Map<Map<Point, Color>, Identifier> cache = new HashMap<>();

    public static Identifier getTexture(Map<Point, Color> glowingEyesMap) {
        Identifier texture = cache.get(glowingEyesMap);
        if (texture != null) {
            return texture;
        }

        texture = createTexture(glowingEyesMap);
        cache.put(glowingEyesMap, texture);
        return texture;
    }

    public static void clear() {
        for(Identifier texture : cache.values()) {
            Minecraft.getInstance().getTextureManager().release(texture);
        }
        cache.clear();
    }

    private static Identifier createTexture(Map<Point, Color> glowingEyesMap) {
        NativeImage image = new NativeImage(64, 64, true);
        for (Map.Entry<Point, Color> entry : glowingEyesMap.entrySet()) {
            Point point = entry.getKey();
            Color color = entry.getValue();
            image.setPixel(point.getX(), point.getY(), color.getRGB());
        }

        String label = "dyntex_" + UUID.randomUUID();
        Identifier id = Util.id(GlowingEyes.MOD_ID, label);
        Minecraft.getInstance().getTextureManager().register(
                id,
                new DynamicTexture(() -> label, image)
        );
        return id;
    }
}
