package me.andreasmelone.glowingeyes.neoforge.common.component.eyes;

import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.Map;

public interface IGlowingEyes extends ValueIOSerializable {
    Map<Point, Color> getGlowingEyesMap();
    void setGlowingEyesMap(Map<Point, Color> glowingEyesMap);
    boolean isToggledOn();
    void setToggledOn(boolean toggledOn);
}
