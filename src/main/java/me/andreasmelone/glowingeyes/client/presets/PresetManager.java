package me.andreasmelone.glowingeyes.client.presets;

import com.google.common.collect.ImmutableList;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.eyes.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.ClientCapabilityMessage;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
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
        for(int i = 1; i < 16 + 1; i++) {
            HashMap<Point, Color> content = new HashMap<>();

            content.put(new Point(i, 0), ModInfo.DEFAULT_EYE_COLOR);
            content.put(new Point(0, i), ModInfo.DEFAULT_EYE_COLOR);

            this.presets.add(new Preset("Preset " + i, i, content));
        }
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

    @Nullable
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
