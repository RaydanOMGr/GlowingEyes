package me.andreasmelone.glowingeyes;

import me.andreasmelone.glowingeyes.common.GlowingEyesEvents;
import me.andreasmelone.glowingeyes.common.packet.CapabilityUpdatePacket;
import net.fabricmc.api.ModInitializer;

import java.awt.*;

public class GlowingEyes implements ModInitializer {
    public static final String MOD_ID = "glowingeyes";
    public static final Color DEFAULT_COLOR = new Color(255, 10, 10, 210);

    @Override
    public void onInitialize() {
        CapabilityUpdatePacket.registerHandlers();
        GlowingEyesEvents.registerEvents();
    }
}
