package me.andreasmelone.glowingeyes.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.*;
import org.slf4j.Logger;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Util {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();

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
            LOGGER.error("An error occured while serializing map to byte array", e);
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
            LOGGER.error("An error occured while deserializing byte array to map", e);
            return null;
        }
    }

    public static <K, V> CompoundTag toCompoundTag(Codec<K> codecK, Codec<V> codecV, Map<K, V> map) {
        CompoundTag tag = new CompoundTag();
        map.forEach((key, value) -> {
            JsonElement encodedKey = codecK.encodeStart(JsonOps.INSTANCE, key).result().orElseThrow();
            Tag encodedValue = codecV.encodeStart(NbtOps.INSTANCE, value).result().orElseThrow();
            tag.put(GSON.toJson(encodedKey), encodedValue);
        });
        return tag;
    }

    public static <K, V> Map<K, V> toMap(Codec<K> codecK, Codec<V> codecV, CompoundTag tag) {
        Map<K, V> map = new LinkedHashMap<>();
        tag.getAllKeys().forEach((key) -> {
            K decodedKey = codecK.decode(JsonOps.INSTANCE, JsonParser.parseString(key)).map(Pair::getFirst).result().orElseThrow();
            V decodedValue = codecV.decode(NbtOps.INSTANCE, tag.get(key)).map(Pair::getFirst).result().orElseThrow();
            map.put(decodedKey, decodedValue);
        });
        return map;
    }

    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (float) tmp / factor;
    }

    public static float round(float value) {
        return round(value, 2);
    }

    public static String sanitizeForId(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "_");
    }

    public static boolean writeToFile(File file, CompoundTag tag) {
        try(OutputStream out = new FileOutputStream(file)) {
            NbtIo.writeCompressed(tag, out);
            return true;
        } catch (IOException e) {
            LOGGER.error("Failed to write data to file!", e);
            return false;
        }
    }

    public static CompoundTag readFromFile(File file) {
        try(InputStream in = new FileInputStream(file)) {
            return NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            LOGGER.error("Failed to write data to file!", e);
            return null;
        }
    }
}