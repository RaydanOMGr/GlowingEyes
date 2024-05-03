package me.andreasmelone.glowingeyes.client;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.commands.EyesCommand;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.render.RenderManager;
import me.andreasmelone.glowingeyes.client.packet.ClientPacketManager;
import me.andreasmelone.glowingeyes.server.scheduler.CodeScheduler;
import me.andreasmelone.glowingeyes.server.scheduler.Scheduler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;

public class GlowingEyesClient implements ClientModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected static final Scheduler CLIENT_SCHEDULER = new CodeScheduler();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Glowing Eyes client setup");
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            EyesCommand.register(dispatcher);
        });

        ClientPacketManager.registerHandlers();
        GlowingEyesKeybindings.register();
        GlowingEyesClientEvents.registerEvents();
        RenderManager.init();

        PresetManager.getInstance().loadPresets();
    }

    public static Scheduler getClientScheduler() {
        return CLIENT_SCHEDULER;
    }
}