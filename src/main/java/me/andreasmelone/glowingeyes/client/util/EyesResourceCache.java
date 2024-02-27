package me.andreasmelone.glowingeyes.client.util;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Dynamic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EyesResourceCache {
    public static final EyesResourceCache INSTANCE = new EyesResourceCache();
    private EyesResourceCache() {
    }

    HashMap<Map<Point, Color>, ResourceLocation> cache = new HashMap<>();

    /**
     * Gets the already existing resource location for the given image, or creates a new one if it doesn't exist
     * @param map The image to get the resource location for
     * @return      The resource location for the given image
     */
    public ResourceLocation get(Map<Point, Color> map) {
        BufferedImage eyeOverlayTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        for (Point point : map.keySet()) {
            Color color = map.get(point);
            eyeOverlayTexture.setRGB(point.x + (64 / 8), point.y + (64 / 8), color.getRGB());
        }

        DynamicTexture dynamicTexture = new DynamicTexture(Util.toNativeImage(eyeOverlayTexture));
        if(cache.containsKey(map)) {
            return cache.get(map);
        } else {
            ResourceLocation resourceLocation
                    = Minecraft.getInstance().getTextureManager().register("eye_overlay_" + UUID.randomUUID(), dynamicTexture);
            cache.put(map, resourceLocation);
            return resourceLocation;
        }
    }
}
