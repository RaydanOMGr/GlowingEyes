package me.andreasmelone.glowingeyes.common.capability.eyes;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;

public class GlowingEyesCapability {
    private GlowingEyesCapability() {
    }

    public static final Capability<IGlowingEyes> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IGlowingEyes.class);
    }

    private static final IGlowingEyes localCapability = new GlowingEyesImpl();
    public static IGlowingEyes getCapability(Player player) {
        if(player.isLocalPlayer()) return localCapability;
        return player.getCapability(INSTANCE)
                .orElseThrow(() -> new IllegalStateException("Could not get GlowingEyes capability from player"));
    }

    @NotNull
    public static HashMap<Point, Color> getGlowingEyesMap(Player player) {
        return getCapability(player).getGlowingEyesMap();
    }

    public static void setGlowingEyesMap(Player player, @NotNull HashMap<Point, Color> glowingEyesMap) {
        getCapability(player).setGlowingEyesMap(glowingEyesMap);
    }

    public static boolean isToggledOn(Player player) {
        return getCapability(player).isToggledOn();
    }

    public static void setToggledOn(Player player, boolean toggledOn) {
        getCapability(player).setToggledOn(toggledOn);
    }
}