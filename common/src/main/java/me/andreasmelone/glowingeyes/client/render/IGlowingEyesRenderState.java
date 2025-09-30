package me.andreasmelone.glowingeyes.client.render;

import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;

import java.util.Map;

public interface IGlowingEyesRenderState {
    /**
     * Gets the glowing eyes map from a player
     *
     * @return the glowing eyes map (Point, Color)
     */
    Map<Point, Color> glowingEyes$getGlowingEyesMap();

    /**
     * Sets the glowing eyes map for the state (does not affect the actual stored data)
     *
     * @param glowingEyesMap the glowing eyes map to set
     */
    void glowingEyes$setGlowingEyesMap(Map<Point, Color> glowingEyesMap);

    /**
     * Gets whether the glowing eyes are toggled on for a player
     *
     * @return whether the glowing eyes are toggled on
     */
    boolean glowingEyes$isToggledOn();

    /**
     * Sets whether the glowing eyes are toggled on for a player (for this state only, not the real data)
     *
     * @param toggledOn whether the glowing eyes are toggled on
     */
    void glowingEyes$setToggledOn(boolean toggledOn);
}
