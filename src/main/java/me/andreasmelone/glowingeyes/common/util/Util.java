package me.andreasmelone.glowingeyes.common.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;

public class Util {
    /**
     * Serialize a map to a byte array
     * @param map the map to serialize
     * @return the byte array containing the serialized map, in case of an error an empty byte array
     * @param <K> the key type of the map, has to be serializable
     * @param <V> the value type of the map, has to be serializable
     */
    public static <K extends Serializable, V extends Serializable> byte[] serializeMap(Map<K, V> map) {
        // use object streams to serialize the map
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(map);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Deserialize a map from a byte array
     * @param serializedMap the byte array containing the serialized map
     * @return the deserialized map, in case of an error null, please make sure to handle properly
     * @param <MAP> the type of the map, including K and V
     * @param <K> the key type of the map, has to be serializable
     * @param <V> the value type of the map, has to be serializable
     */
    public static <MAP extends Map<K, V>, K extends Serializable, V extends Serializable> MAP deserializeMap(byte[] serializedMap) {
        // use object streams to deserialize the map
        try(ByteArrayInputStream bais = new ByteArrayInputStream(serializedMap)) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (MAP) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static NativeImage toNativeImage(BufferedImage image) {
        NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), true);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);

                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                int rgba = (blue << 24) | (red << 16) | (green << 8) | alpha;
                nativeImage.setPixelRGBA(x, y, rgba);
            }
        }

        return nativeImage;
    }
}
