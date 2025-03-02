package me.andreasmelone.glowingeyes.client.presets.serialize.gson;

import com.google.gson.*;
import me.andreasmelone.glowingeyes.common.util.Color;

import java.lang.reflect.Type;

public class ColorSerializer implements JsonSerializer<Color>, JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Color(json.getAsInt());
    }

    @Override
    public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getRGB());
    }
}
