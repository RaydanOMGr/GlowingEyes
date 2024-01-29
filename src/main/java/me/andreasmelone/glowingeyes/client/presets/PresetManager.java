package me.andreasmelone.glowingeyes.client.presets;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.eyes.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.ClientCapabilityMessage;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

// PresetManager
// The initial idea was to have an array list, but this does not work due to how ids are assigned.
// The ids are do not have to be by the order and can be absolutely random, so an array list is not
// the best choice.
// A hash map would be the best choice, I will implement this as soon as I have time.
public class PresetManager {
    private static final PresetManager INSTANCE = new PresetManager();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(HashMap.class, new PointColorMapSerializer())
            .create();

    private final File presetStorage = new File("presets.json");
    private final HashMap<Integer, Preset> presets = new HashMap<>();

    public void loadPresets() {
        if(!this.presetStorage.exists() || !this.presetStorage.isFile()) {
            GlowingEyes.logger.info("No presets file found, creating a new one");
            this.saveDefaultPresets();
            return;
        }

        GsonPresetsFileModel gsonPresetsFileModel;
        try {
            gsonPresetsFileModel = gson.fromJson(
                    new String(Files.readAllBytes(this.presetStorage.toPath())),
                    GsonPresetsFileModel.class
            );
        } catch (JsonSyntaxException e) {
            GlowingEyes.logger.error("Could not parse presets file due to it being malformed. Not loading data.");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            GlowingEyes.logger.error("Could not load presets file due to an IOException");
            e.printStackTrace();
            return;
        }

        for(Preset preset : gsonPresetsFileModel.getPresets()) {
            this.presets.put(preset.getId(), preset);
        }

        GlowingEyes.logger.info("Loaded " + this.presets.size() + " presets");
    }

    public void savePresets() {
        GsonPresetsFileModel gsonPresetsFileModel = new GsonPresetsFileModel(this.presets.values().toArray(new Preset[0]));
        String json = gson.toJson(gsonPresetsFileModel);
        GlowingEyes.logger.info("Saving presets file");
        GlowingEyes.logger.debug("Saving presets file with content: " + json);
        try {
            Files.write(this.presetStorage.toPath(), json.getBytes());
        } catch (IOException e) {
            GlowingEyes.logger.error("Could not save presets file due to an IOException");
            e.printStackTrace();
        }
    }

    public void saveDefaultPresets() {
        // extract the presets.json file from the jar
        try(InputStream presetStream = getClass().getClassLoader().getResourceAsStream("presets.json")) {
            if(presetStream == null) {
                GlowingEyes.logger.error("Could not save default presets file due to it not being found in the jar");
                return;
            }
            Path dest = presetStorage.toPath();
            Files.copy(presetStream, dest);
        } catch (IOException e) {
            GlowingEyes.logger.error("Could not save default presets file due to an IOException");
            e.printStackTrace();
            return;
        }
        this.loadPresets();
    }

    public HashMap<Integer, Preset> getPresets() {
        return new HashMap<>(this.presets);
    }

    public List<Preset> getPresetList() {
        return ImmutableList.copyOf(this.presets.values());
    }

    public void applyPreset(int id) {
        if(!hasPreset(id)) {
            GlowingEyes.logger.error("Tried to apply preset with id " + id + ", but it does not exists ");
            return;
        }
        Preset preset = this.presets.get(id);

        GlowingEyes.proxy.getPixelMap().clear();
        GlowingEyes.proxy.getPixelMap().putAll(preset.getContent());

        EntityPlayer player = GlowingEyes.proxy.getPlayer();
        if(player != null) {
            IGlowingEyesCapability cap = player.getCapability(GlowingEyesProvider.CAPABILITY, null);
            if(cap == null) return;

            cap.setGlowingEyesMap(preset.getContent());
            NetworkHandler.sendToServer(new ClientCapabilityMessage(cap, player));
        }
    }

    public boolean hasPreset(int id) {
        if(id < 0) return false;
        for(int element : this.presets.keySet()) {
            if(element == id) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Preset getPreset(int id) {
        if(!hasPreset(id)) {
            return null;
        }
        return this.presets.get(id);
    }

    public boolean hasPage(int page, int pageSize) {
        // a page can have even only one element, but still be valid
        return page >= 0 && page <= ((this.presets.size() - 1) / pageSize);
    }

    public int addPreset(Preset preset) {
        this.presets.put(preset.getId(), preset);
        return this.presets.size() - 1;
    }

    public int createPreset(String name, HashMap<Point, Color> content) {
        return this.createPreset(name, content, findId(this.presets.size()));
    }

    private int findId(int id) {
        if(!hasPreset(id)) {
            return id;
        }
        return findId(id + 1);
    }

    public int createPreset(String name, HashMap<Point, Color> content, int id) {
        int minX = 0;
        int minY = 0;
        int maxX = 15;
        int maxY = 15;

        HashMap<Point, Color> contentCopy = new HashMap<>(content);
        // check if the content has pixels outside range x 0, y 0 - x 16, y 16
        for(Point point : contentCopy.keySet()) {
            if(point.x < minX || point.x > maxX || point.y < minY || point.y > maxY) {
                GlowingEyes.logger.error(String.format(
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

    public void removePreset(int id) {
        if(!hasPreset(id)) {
            return;
        }
        this.presets.remove(id);
    }

    public int getFreeId() {
        for(int i = 0; i < this.presets.size(); i++) {
            if(this.presets.get(i) == null) {
                return i;
            }
        }
        return this.presets.size();
    }

    public static PresetManager getInstance() {
        return INSTANCE;
    }
    private PresetManager() {}
}
