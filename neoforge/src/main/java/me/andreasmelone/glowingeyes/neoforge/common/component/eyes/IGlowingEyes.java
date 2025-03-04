package me.andreasmelone.glowingeyes.neoforge.common.component.eyes;

import java.util.Map;

import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IGlowingEyes extends INBTSerializable<CompoundTag> {
    Map<Point, Color> getGlowingEyesMap();
    void setGlowingEyesMap(Map<Point, Color> glowingEyesMap);
    boolean isToggledOn();
    void setToggledOn(boolean toggledOn);
}
