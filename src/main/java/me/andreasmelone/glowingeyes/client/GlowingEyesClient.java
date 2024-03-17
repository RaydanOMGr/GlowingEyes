package me.andreasmelone.glowingeyes.client;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.commands.EyesCommand;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GlowingEyesClient implements ClientModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected static final Scheduler CLIENT_SCHEDULER = new CodeScheduler();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Glowing Eyes client setup");
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            EyesCommand.register(dispatcher);
        });
        GlowingEyesClientEvents.registerEvents();

        PresetManager.getInstance().loadPresets();
    }

    public static Scheduler getClientScheduler() {
        return CLIENT_SCHEDULER;
    }
}