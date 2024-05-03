package me.andreasmelone.glowingeyes.server.component.eyes;

import me.andreasmelone.glowingeyes.server.util.Util;
import net.minecraft.nbt.CompoundTag;

import java.awt.*;
import java.util.HashMap;

public class GlowingEyesImpl implements IGlowingEyes {
    private boolean toggledOn = true;
    private HashMap<Point, Color> glowingEyesMap = new HashMap<>();

    @Override
    public HashMap<Point, Color> getGlowingEyesMap() {
        return this.glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMap(HashMap<Point, Color> glowingEyesMap) {
        this.glowingEyesMap = glowingEyesMap;
    }

    @Override
    public boolean isToggledOn() {
        return toggledOn;
    }

    @Override
    public void setToggledOn(boolean toggledOn) {
        this.toggledOn = toggledOn;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        setToggledOn(tag.getBoolean("toggledOn"));
        setGlowingEyesMap(Util.deserializeMap(tag.getByteArray("glowingEyesMap")));
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("toggledOn", isToggledOn());
        tag.putByteArray("glowingEyesMap", Util.serializeMap(getGlowingEyesMap()));
    }
}
