package me.andreasmelone.glowingeyes.client.presets;

import com.google.common.collect.ImmutableList;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.eyes.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.ClientCapabilityMessage;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PresetManager {
    private static final PresetManager INSTANCE = new PresetManager();

    private final File presetStorage = new File("presets.json");
    private final List<Preset> presets = new LinkedList<>();

    public void loadPresets() {
        HashMap<Point, Color> content = new HashMap<>();
        content.put(new Point(3, 5), ModInfo.DEFAULT_EYE_COLOR);
        content.put(new Point(6, 5), ModInfo.DEFAULT_EYE_COLOR);
        presets.add(new Preset("Eyes 1", content));

        content.put(new Point(3, 6), ModInfo.DEFAULT_EYE_COLOR);
        content.put(new Point(6, 6), ModInfo.DEFAULT_EYE_COLOR);
        presets.add(new Preset("Eyes 2", content));
    }

    public void savePresets() {

    }

    public List<Preset> getPresets() {
        return ImmutableList.copyOf(this.presets);
    }
    public void applyPreset(int id) {
        if(id < 0 || id >= this.presets.size()) {
            return;
        }
        Preset preset = this.presets.get(id);
        GlowingEyes.proxy.getPixelMap().clear();
        GlowingEyes.proxy.getPixelMap().putAll(preset.getContent());
        EntityPlayer player = GlowingEyes.proxy.getPlayer();
        if(player != null) {
            IGlowingEyesCapability cap = player.getCapability(GlowingEyesProvider.CAPABILITY, null);
            if(cap == null) return;
            cap.setGlowingEyesMap(GlowingEyes.proxy.getPixelMap());
            NetworkHandler.sendToServer(new ClientCapabilityMessage(cap, player));
        }
    }

    public Preset getPreset(int id) {
        if(id < 0 || id >= this.presets.size()) {
            return null;
        }
        return this.presets.get(id);
    }

    public void addPreset(Preset preset) {
        this.presets.add(preset);
    }


    public static PresetManager getInstance() {
        return INSTANCE;
    }
    private PresetManager() {}
}
