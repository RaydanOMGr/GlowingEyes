package me.andreasmelone.glowingeyes.fabric.common.component.eyes;

import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.Map;

public interface IGlowingEyes extends Component {
    Map<Point, Color> getGlowingEyesMap();
    void setGlowingEyesMap(Map<Point, Color> glowingEyesMap);
    boolean isToggledOn();
    void setToggledOn(boolean toggledOn);
}
