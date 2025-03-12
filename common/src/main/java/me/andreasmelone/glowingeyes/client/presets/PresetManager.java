package me.andreasmelone.glowingeyes.client.presets;

import com.google.gson.*;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.presets.serialize.GlowingEyesReferences;
import me.andreasmelone.glowingeyes.client.presets.serialize.SchemaV0;
import me.andreasmelone.glowingeyes.client.presets.serialize.fix.IdFormatFix;
import me.andreasmelone.glowingeyes.client.presets.serialize.fix.PointRangeFix;
import me.andreasmelone.glowingeyes.client.presets.serialize.fix.PresetFile;
import me.andreasmelone.glowingeyes.client.presets.serialize.gson.ColorSerializer;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;

public class PresetManager {
    private static final int DATA_VERSION = 3;
    private static final PresetManager INSTANCE = new PresetManager();
    private static final Logger LOGGER = LogUtils.getLogger();

    private final DataFixer dfu = createDFU();
    private final File presetStorage = new File("presets.json");
    private final Map<ResourceLocation, Preset> presets = new LinkedHashMap<>();

    public void loadPresets() {
        if (!this.presetStorage.exists() || !this.presetStorage.isFile()) {
            LOGGER.info("No presets file found, creating a new one");
            this.saveDefaultPresets();
            return;
        }
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Color.class, new ColorSerializer())
                .create();
        PresetFile presets;

        try (InputStream in = new FileInputStream(presetStorage);
                InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(reader)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String content = sb.toString();
            Dynamic<JsonElement> dynamicData = new Dynamic<>(JsonOps.INSTANCE, JsonParser.parseString(content));
            var result = PresetFile.CODEC.decode(dynamicData);
            if (result.error().isPresent()) {
                throw new RuntimeException("Failed to decode preset file " + result.error().get().message());
            }

            presets = result.map(Pair::getFirst).result().orElseThrow();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the JSON file", e);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Presets not found in preset file", e);
        }

        int version = presets.dataVersion();
        for (Preset preset : presets.presets()) {
            Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Preset.CODEC.encodeStart(JsonOps.INSTANCE, preset).result().orElse(new JsonObject()));
            dynamic = dfu.update(GlowingEyesReferences.PRESET, dynamic, version, DATA_VERSION);
            var updatedPresetResult = Preset.CODEC.decode(dynamic);
            if(updatedPresetResult.error().isPresent()) {
                throw new RuntimeException("Was unable to decode updated preset: " + updatedPresetResult.error().get());
            }

            Optional<Preset> updatedPreset = updatedPresetResult.map(Pair::getFirst).result();
            if(updatedPreset.isEmpty()) {
                throw new RuntimeException("Was unable to decode updated preset for unknown reason");
            }

            this.presets.put(preset.getId(), updatedPreset.get());
        }

        LOGGER.info("Loaded {} presets", this.presets.size());
    }

    public void savePresets() {
        PresetFile presets = new PresetFile(DATA_VERSION, new ArrayList<>(this.presets.values()));
        DataResult<JsonElement> serializedPresets = PresetFile.CODEC.encodeStart(JsonOps.INSTANCE, presets);
        if(serializedPresets.error().isPresent()) {
            throw new RuntimeException("Couldn't serialize presets " + serializedPresets.error().get());
        }
        if(serializedPresets.result().isEmpty()) {
            throw new RuntimeException("Couldn't serialize presets for unknown reason");
        }
        String json = serializedPresets.result().get().toString();
        LOGGER.info("Saving presets file");
        LOGGER.debug("Saving presets file with content: {}", json);
        try {
            Files.write(this.presetStorage.toPath(), json.getBytes());
        } catch (IOException e) {
            LOGGER.error("Could not save presets file due to an IOException", e);
        }
    }

    public void saveDefaultPresets() {
        // extract the presets.json file from the jar
        try (InputStream presetStream = getClass().getClassLoader().getResourceAsStream("presets.json")) {
            if (presetStream == null) {
                LOGGER.error("Could not save default presets file due to it not being found in the jar");
                return;
            }
            Path dest = presetStorage.toPath();
            Files.copy(presetStream, dest);
        } catch (IOException e) {
            LOGGER.error("Could not save default presets file due to an IOException", e);
            return;
        }
        this.loadPresets();
    }

    public List<Preset> getPresets() {
        return new ArrayList<>(this.presets.values());
    }

    public void applyPreset(ResourceLocation id) {
        if (!hasPreset(id)) {
            LOGGER.error("Tried to apply preset with id {}, but it does not exists ", id);
            return;
        }
        Preset preset = this.presets.get(id);

        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, preset.getContent());
        ClientGlowingEyesComponent.sendUpdate();
    }

    public boolean hasPreset(ResourceLocation id) {
        return this.presets.get(id) != null;
    }

    public boolean hasPreset(int id) {
        int i = 0;
        for (ResourceLocation presetId : this.presets.keySet()) {
            if (i == id) {
                return true;
            }
            i++;
        }
        return false;
    }

    public Preset getPreset(ResourceLocation id) {
        if (!hasPreset(id)) {
            return null;
        }
        return this.presets.get(id);
    }

    public Preset getPreset(int id) {
        int i = 0;
        for (ResourceLocation presetId : this.presets.keySet()) {
            if (i == id) {
                return this.presets.get(presetId);
            }
            i++;
        }
        return null;
    }

    public boolean hasPage(int page, int pageSize) {
        // a page can have even only one element but still be valid
        return page >= 0 && page <= ((this.presets.size() - 1) / pageSize);
    }

    public int addPreset(Preset preset) {
        return addPreset(preset, 0);
    }

    private int addPreset(Preset preset, int number) {
        ResourceLocation id = preset.getId();
        if (number > 0) {
            id = new ResourceLocation(preset.getId().getNamespace(), preset.getId().getPath() + "_" + number);
        }
        if (presets.get(id) != null) {
            return addPreset(preset, number + 1);
        }

        this.presets.put(id, new Preset(preset.getName(), id, preset.getContent()));
        return this.presets.size() - 1;
    }

    public int createPreset(String name, Map<Point, Color> content) {
        return this.createPreset(name, content, new ResourceLocation(GlowingEyes.MOD_ID, "preset_" + Util.sanitizeForId(name)));
    }

    public int createPreset(String name, Map<Point, Color> content, ResourceLocation id) {
        int minX = 0;
        int minY = 0;
        int maxX = 63;
        int maxY = 63;

        Map<Point, Color> contentCopy = new HashMap<>(content);
        // check if the content has pixels outside range x 0, y 0 - x 16, y 16
        for (Point point : content.keySet()) {
            if (point.getX() < minX || point.getX() > maxX || point.getY() < minY || point.getY() > maxY) {
                LOGGER.error("Tried to create preset with id {}, but the content has pixels outside of range {}, {} - {}, {}", id, minX, minY, maxX, maxY);
                // remove the invalid point
                contentCopy.remove(point);
            }
        }
        return this.addPreset(new Preset(name, id, contentCopy));
    }

    public void removePreset(ResourceLocation id) {
        if (!hasPreset(id)) {
            return;
        }
        this.presets.remove(id);
    }

    public static PresetManager getInstance() {
        return INSTANCE;
    }

    private static DataFixer createDFU() {
        DataFixerBuilder builder = new DataFixerBuilder(DATA_VERSION);
        BiFunction<Integer, Schema, Schema> same = Schema::new;

        builder.addSchema(0, SchemaV0::new);
        Schema schemaV1 = builder.addSchema(1, same);
        builder.addFixer(new IdFormatFix(schemaV1, true));
        Schema schemaV2 = builder.addSchema(2, same);
        builder.addFixer(new PointRangeFix(schemaV2, true));
        Schema schemaV3 = builder.addSchema(3, same);

        return builder.buildUnoptimized();
    }
}