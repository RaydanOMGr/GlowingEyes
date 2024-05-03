package me.andreasmelone.glowingeyes.server.component.eyes;

import dev.onyxstudios.cca.api.v3.component.Component;

import java.awt.*;
import java.util.HashMap;

public interface IGlowingEyes extends Component {
    HashMap<Point, Color> getGlowingEyesMap();
    void setGlowingEyesMap(HashMap<Point, Color> glowingEyesMap);
    boolean isToggledOn();
    void setToggledOn(boolean toggledOn);
}
