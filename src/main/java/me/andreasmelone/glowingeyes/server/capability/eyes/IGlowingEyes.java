package me.andreasmelone.glowingeyes.server.capability.eyes;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;

public interface IGlowingEyes extends Serializable {
    @Nonnull
    HashMap<Point, Color> getGlowingEyesMap();
    void setGlowingEyesMap(@Nonnull HashMap<Point, Color> glowingEyesMap);
    boolean isToggledOn();
    void setToggledOn(boolean toggledOn);
}
