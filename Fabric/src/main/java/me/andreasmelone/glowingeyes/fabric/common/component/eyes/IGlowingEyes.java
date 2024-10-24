package me.andreasmelone.glowingeyes.fabric.common.component.eyes;

import dev.onyxstudios.cca.api.v3.component.Component;

import java.awt.Color;
import java.awt.Point;
import java.util.Map;

public interface IGlowingEyes extends Component {
    Map<Point, Color> getGlowingEyesMap();
    void setGlowingEyesMap(Map<Point, Color> glowingEyesMap);
    boolean isToggledOn();
    void setToggledOn(boolean toggledOn);
}
