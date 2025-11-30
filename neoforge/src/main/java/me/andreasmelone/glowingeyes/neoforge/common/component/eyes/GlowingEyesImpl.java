package me.andreasmelone.glowingeyes.neoforge.common.component.eyes;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class GlowingEyesImpl implements IGlowingEyes {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Codec<Map<Point, Color>> MAP_CODEC = Codec.unboundedMap(Point.CODEC_STRING, Color.CODEC);

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
        return this.toggledOn;
    }

    @Override
    public void setToggledOn(boolean toggledOn) {
        this.toggledOn = toggledOn;
    }

    @Override
    public void deserialize(@NotNull ValueInput tag) {
        try {
            this.setToggledOn(tag.getBooleanOr("toggledOn", true));
            this.setGlowingEyesMap(tag.read("glowingEyesMap", MAP_CODEC).orElse(new HashMap<>()));
        } catch (Exception e) {
            LOGGER.error("Failed to load player data! Data will be reset", e);
        }
    }

    @Override
    public void serialize(@NotNull ValueOutput tag) {
        tag.putBoolean("toggledOn", this.isToggledOn());
        tag.store("glowingEyesMap", MAP_CODEC, this.glowingEyesMap);
    }
}
