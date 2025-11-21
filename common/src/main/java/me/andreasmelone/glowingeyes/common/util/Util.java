package me.andreasmelone.glowingeyes.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Util {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();

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
        tag.keySet().forEach((key) -> {
            JsonElement input;
            try {
                input = JsonParser.parseString(key);
            } catch (JsonSyntaxException e) {
                LOGGER.error("Failed to parse key {}", key, e);
                return;
            }
            K decodedKey = codecK.decode(JsonOps.INSTANCE, input).map(Pair::getFirst).result().orElseThrow();
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

    public static ResourceLocation id(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static <T, U> StreamCodec<T, U> emptyStreamCodec(Supplier<U> constructor) {
        return new StreamCodec<>() {
            @Override
            public @NotNull U decode(@NotNull T t) {
                return constructor.get();
            }

            @Override
            public void encode(@NotNull T o, @NotNull U u) {

            }
        };
    }

    public static <U> StreamCodec<ByteBuf, U> createStreamCodec(Function<FriendlyByteBuf, U> decoder, BiConsumer<FriendlyByteBuf, U> encoder) {
        return new StreamCodec<>() {
            @Override
            public void encode(@NotNull ByteBuf buffer, @NotNull U object) {
                FriendlyByteBuf wrapper = new FriendlyByteBuf(buffer);
                encoder.accept(wrapper, object);
            }

            @Override
            public @NotNull U decode(@NotNull ByteBuf buffer) {
                FriendlyByteBuf wrapper = new FriendlyByteBuf(buffer);
                return decoder.apply(wrapper);
            }
        };
    }

    public static byte[] toByteArray(ByteBuf buf) {
        int length = buf.readableBytes();
        byte[] bytes = new byte[length];

        buf.getBytes(buf.readerIndex(), bytes);

        return bytes;
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