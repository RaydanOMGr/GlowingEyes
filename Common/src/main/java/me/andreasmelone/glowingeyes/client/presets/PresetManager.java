package me.andreasmelone.glowingeyes.client.presets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.presets.gson.GsonPresetsFileModel;
import me.andreasmelone.glowingeyes.client.presets.gson.OldToNewMigrator;
import me.andreasmelone.glowingeyes.client.presets.gson.PointColorMapSerializer;
import me.andreasmelone.glowingeyes.client.presets.gson.ResourceLocationSerializer;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class PresetManager {
    private static final PresetManager INSTANCE = new PresetManager();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(HashMap.class, new PointColorMapSerializer())
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationSerializer())
            .setPrettyPrinting()
            .create();
    Logger logger = LogUtils.getLogger();

    private final File presetStorage = new File("presets.json");
    private final Map<ResourceLocation, Preset> presets = new LinkedHashMap<>();

    public void loadPresets() {
        if(!this.presetStorage.exists() || !this.presetStorage.isFile()) {
            logger.info("No presets file found, creating a new one");
            this.saveDefaultPresets();
            return;
        }

        if(OldToNewMigrator.isOldFormat(this.presetStorage)) {
            logger.info("Detected old format of presets file, migrating to new format");
            GsonPresetsFileModel gsonPresetsFileModel = OldToNewMigrator.migrate(this.presetStorage);
            String json = gson.toJson(gsonPresetsFileModel);
            logger.info("Saving migrated presets file");
            logger.debug("Saving migrated presets file with content: {}", json);
            try {
                Files.write(this.presetStorage.toPath(), json.getBytes());
            } catch (IOException e) {
                logger.error("Could not save migrated presets file due to an IOException", e);
            }
        }

        GsonPresetsFileModel gsonPresetsFileModel;
        try {
            gsonPresetsFileModel = gson.fromJson(
                    new String(Files.readAllBytes(this.presetStorage.toPath())),
                    GsonPresetsFileModel.class
            );
        } catch (JsonSyntaxException e) {
            logger.error("Could not parse presets file due to it being malformed. Not loading data.", e);
            return;
        } catch (IOException e) {
            logger.error("Could not load presets file due to an IOException", e);
            return;
        }

        for(Preset preset : gsonPresetsFileModel.getPresets()) {
            this.presets.put(preset.getId(), preset);
        }

        logger.info("Loaded {} presets", this.presets.size());
    }

    public void savePresets() {
        GsonPresetsFileModel gsonPresetsFileModel = new GsonPresetsFileModel(this.presets.values().toArray(new Preset[0]));
        String json = gson.toJson(gsonPresetsFileModel);
        logger.info("Saving presets file");
        logger.debug("Saving presets file with content: {}", json);
        try {
            Files.write(this.presetStorage.toPath(), json.getBytes());
        } catch (IOException e) {
            logger.error("Could not save presets file due to an IOException", e);
        }
    }

    public void saveDefaultPresets() {
        // extract the presets.json file from the jar
        try(InputStream presetStream = getClass().getClassLoader().getResourceAsStream("presets.json")) {
            if(presetStream == null) {
                logger.error("Could not save default presets file due to it not being found in the jar");
                return;
            }
            Path dest = presetStorage.toPath();
            Files.copy(presetStream, dest);
        } catch (IOException e) {
            logger.error("Could not save default presets file due to an IOException", e);
            return;
        }
        this.loadPresets();
    }

    public List<Preset> getPresets() {
        return new ArrayList<>(this.presets.values());
    }

    public void applyPreset(ResourceLocation id) {
        if(!hasPreset(id)) {
            logger.error("Tried to apply preset with id {}, but it does not exists ", id);
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
        for(ResourceLocation presetId : this.presets.keySet()) {
            if(i == id) {
                return true;
            }
            i++;
        }
        return false;
    }

    public Preset getPreset(ResourceLocation id) {
        if(!hasPreset(id)) {
            return null;
        }
        return this.presets.get(id);
    }

    public Preset getPreset(int id) {
        int i = 0;
        for(ResourceLocation presetId : this.presets.keySet()) {
            if(i == id) {
                return this.presets.get(presetId);
            }
            i++;
        }
        return null;
    }

    public boolean hasPage(int page, int pageSize) {
        // a page can have even only one element, but still be valid
        return page >= 0 && page <= ((this.presets.size() - 1) / pageSize);
    }

    public int addPreset(Preset preset) {
        return addPreset(preset, 0);
    }

    private int addPreset(Preset preset, int number) {
        ResourceLocation id = preset.getId();
        if(number > 0) {
            id = new ResourceLocation(preset.getId().getNamespace(), preset.getId().getPath() + "_" + number);
        }
        if(presets.get(id) != null) {
            return addPreset(preset, number + 1);
        }

        this.presets.put(id, new Preset(preset.getName(), id, preset.getContent()));
        return this.presets.size() - 1;
    }

    public int createPreset(String name, HashMap<Point, Color> content) {
        return this.createPreset(name, content, new ResourceLocation(GlowingEyes.MOD_ID, "preset_" + sanitizeForId(name)));
    }

    public int createPreset(String name, HashMap<Point, Color> content, ResourceLocation id) {
        int minX = 0;
        int minY = 0;
        int maxX = 15;
        int maxY = 15;

        HashMap<Point, Color> contentCopy = new HashMap<>(content);
        // check if the content has pixels outside range x 0, y 0 - x 16, y 16
        for(Point point : contentCopy.keySet()) {
            if(point.x < minX || point.x > maxX || point.y < minY || point.y > maxY) {
                logger.error(String.format(
                        "Tried to create preset with id %s, but the content has pixels outside of range %s, %s - %s, %s",
                        id, minX, minY, maxX, maxY
                ));
                // replace it by the closest valid point
                contentCopy.remove(point);
                contentCopy.put(new Point(
                        Math.max(minX, Math.min(maxX, point.x)),
                        Math.max(minY, Math.min(maxY, point.y))
                ), contentCopy.get(point));
            }
        }
        return this.addPreset(new Preset(name, id, contentCopy));
    }

    public void removePreset(ResourceLocation id) {
        if(!hasPreset(id)) {
            return;
        }
        this.presets.remove(id);
    }

    private String sanitizeForId(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "_");
    }

    public static PresetManager getInstance() {
        return INSTANCE;
    }
    private PresetManager() {}
}