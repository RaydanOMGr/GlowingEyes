package me.andreasmelone.glowingeyes.neoforge.common.component.eyes;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class GlowingEyesImpl implements IGlowingEyes {
    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean toggledOn = true;
    private Map<Point, Color> glowingEyesMap = new HashMap<>();

    @Nonnull
    @Override
    public Map<Point, Color> getGlowingEyesMap() {
        return this.glowingEyesMap;
    }

    @Override
    public void setGlowingEyesMap(@Nonnull Map<Point, Color> glowingEyesMap) {
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
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("toggledOn", isToggledOn());
        tag.put("glowingEyesMap", Util.toCompoundTag(Point.CODEC_STRING, Color.CODEC, getGlowingEyesMap()));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        setToggledOn(compoundTag.getBoolean("toggledOn"));
        if (compoundTag.get("glowingEyesMap") instanceof ByteArrayTag) {
            setGlowingEyesMap(new HashMap<>());
            LOGGER.warn("Detected glowing eyes map of old format!");
            LOGGER.warn("Your current eyes will be erased.");
            return;
        }
        setGlowingEyesMap(Util.toMap(Point.CODEC_STRING, Color.CODEC, compoundTag.getCompound("glowingEyesMap")));
    }
}
