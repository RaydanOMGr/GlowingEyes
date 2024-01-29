package me.andreasmelone.glowingeyes.client.presets;

import com.google.gson.*;
import me.andreasmelone.glowingeyes.GlowingEyes;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PointColorMapSerializer implements JsonSerializer<Map<Point, Color>>, JsonDeserializer<Map<Point, Color>> {
    @Override
    public JsonElement serialize(Map<Point, Color> map, Type type, JsonSerializationContext ctx) {
        JsonObject jsonObject = new JsonObject();
        for (Point point : map.keySet()) {
            Color color = map.get(point);
            jsonObject.addProperty(point.x + "," + point.y, color.getRGB());
        }
        return jsonObject;
    }

    @Override
    public HashMap<Point, Color> deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
        HashMap<Point, Color> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : entries) {
            String[] split = entry.getKey().split(",");
            if (split.length != 2) {
                GlowingEyes.logger.error("Could not parse presets file due to it being malformed. Not loading data.");
                throw new JsonParseException("Could not parse presets file due to it being malformed. Not loading data.");
            }
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int rgb = entry.getValue().getAsInt();
            map.put(new Point(x, y), new Color(rgb));
        }
        return map;
    }
}
