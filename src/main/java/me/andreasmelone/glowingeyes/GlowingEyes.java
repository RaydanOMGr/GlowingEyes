package me.andreasmelone.glowingeyes;

import me.andreasmelone.glowingeyes.server.GlowingEyesEvents;
import me.andreasmelone.glowingeyes.server.packet.PacketManager;
import me.andreasmelone.glowingeyes.server.scheduler.CodeScheduler;
import me.andreasmelone.glowingeyes.server.scheduler.Scheduler;
import net.fabricmc.api.ModInitializer;

import java.awt.*;

public class GlowingEyes implements ModInitializer {
    public static final String MOD_ID = "glowingeyes";
    public static final Color DEFAULT_COLOR = new Color(255, 10, 10, 210);
    private static final Scheduler SERVER_SCHEDULER = new CodeScheduler();

    @Override
    public void onInitialize() {
        PacketManager.registerHandlers();
        GlowingEyesEvents.registerEvents();
    }

    public static Scheduler getServerScheduler() {
        return SERVER_SCHEDULER;
    }
}
