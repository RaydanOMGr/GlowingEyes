package me.andreasmelone.glowingeyes.fabric.common.component.eyes;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GlowingEyesImpl implements IGlowingEyes {
    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean toggledOn = true;
    private Map<Point, Color> glowingEyesMap = new HashMap<>();

    public GlowingEyesImpl() {
    }

    public GlowingEyesImpl(boolean toggledOn, Map<Point, Color> glowingEyesMap) {
        this.toggledOn = toggledOn;
        this.glowingEyesMap = glowingEyesMap;
    }

    @Override
    public Map<Point, Color> getGlowingEyesMap() {
        return this.glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMap(Map<Point, Color> glowingEyesMap) {
        this.glowingEyesMap = glowingEyesMap;
    }

    @Override
    public boolean isToggledOn() {
        return this.toggledOn;
    }

    @Override
    public void setToggledOn(boolean toggledOn) {
        this.toggledOn = toggledOn;
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.@NotNull Provider registryLookup) {
        try {
            this.setToggledOn(tag.getBoolean("toggledOn").orElse(true));
            if (tag.get("glowingEyesMap") instanceof ByteArrayTag) {
                this.setGlowingEyesMap(new HashMap<>());
                LOGGER.warn("Detected glowing eyes map of old format!");
                LOGGER.warn("Your current eyes will be erased.");
                return;
            }
            this.setGlowingEyesMap(Util.toMap(Point.CODEC_STRING, Color.CODEC, tag.getCompound("glowingEyesMap").orElse(new CompoundTag())));
        } catch (Exception e) {
            LOGGER.error("Failed to load player data! Data will be reset", e);
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.@NotNull Provider registryLookup) {
        tag.putBoolean("toggledOn", this.isToggledOn());
        tag.put("glowingEyesMap", Util.toCompoundTag(Point.CODEC_STRING, Color.CODEC, this.getGlowingEyesMap()));
    }

    @Override
    public String toString() {
        return "GlowingEyesImpl{" +
                "toggledOn=" + this.toggledOn +
                ", glowingEyesMap=" + this.glowingEyesMap +
                '}';
    }
}
