package me.andreasmelone.glowingeyes.client.presets.gson;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.presets.Preset;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The old format of presets had a numeric ID, now it uses a string (ResourceLocation)
 * This is a migrator that detects and converts old presets to the new format
 */
public class OldToNewMigrator {
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(HashMap.class, new PointColorMapSerializer())
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationSerializer())
            .setPrettyPrinting()
            .create();

    public static boolean isOldFormat(File file) {
        try(InputStream is = new FileInputStream(file)) {
            JsonObject jsonObject = gson.fromJson(new InputStreamReader(is), JsonObject.class);
            JsonArray presets = jsonObject.getAsJsonArray("presets");
            for(JsonElement preset : presets) {
                JsonObject presetObject = preset.getAsJsonObject();
                if(presetObject.has("id")
                        && presetObject.get("id").isJsonPrimitive()
                        && presetObject.get("id").getAsJsonPrimitive().isNumber())
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static GsonPresetsFileModel migrate(File file) {
        try(InputStream is = new FileInputStream(file)) {
            GsonPresetsFileModel oldModel = gson.fromJson(new InputStreamReader(is), GsonPresetsFileModel.class);
            List<Preset> newPresets = new ArrayList<>();
            for(Preset oldPreset : oldModel.getPresets()) {
                newPresets.add(migrate(oldPreset));
            }
            return new GsonPresetsFileModel(newPresets.toArray(new Preset[0]));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Preset migrate(Preset oldPreset) {
        return new Preset(
                oldPreset.getName(),
                new ResourceLocation(GlowingEyes.MOD_ID, oldPreset.getName().toLowerCase().replace(" ", "_")),
                oldPreset.getContent()
        );
    }

    public static List<JsonElement> asList(JsonArray jsonArray) {
        List<JsonElement> list = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            list.add(element);
        }
        return ImmutableList.copyOf(list);
    }
}
