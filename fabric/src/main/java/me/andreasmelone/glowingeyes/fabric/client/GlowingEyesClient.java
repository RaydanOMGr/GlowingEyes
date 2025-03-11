package me.andreasmelone.glowingeyes.fabric.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.commands.EyesCommand;
import me.andreasmelone.glowingeyes.client.commands.GlowingEyesInfoCommand;
import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.mod.ClientModVariables;
import me.andreasmelone.glowingeyes.fabric.client.component.data.ClientPlayerDataComponentImpl;
import me.andreasmelone.glowingeyes.fabric.client.component.eyes.ClientGlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.fabric.client.render.RenderManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.slf4j.Logger;

public class GlowingEyesClient implements ClientModInitializer, ClientModContext {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ClientModVariables variables = new ClientModVariables();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Glowing Eyes client setup");

        ClientGlowingEyesComponent.setImplementation(new ClientGlowingEyesComponentImpl());
        ClientPlayerDataComponent.setImplementation(new ClientPlayerDataComponentImpl());

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> {
            new EyesCommand<>(GlowingEyesClient::createArgumentBuilder).register(this, dispatcher);
            new GlowingEyesInfoCommand<>(GlowingEyesClient::createArgumentBuilder).register(this, dispatcher);
        });

        GlowingEyesKeybindings.register(this);
        GlowingEyesClientEvents.registerEvents();
        RenderManager.init();

        PresetManager.getInstance().loadPresets();
    }

    @Override
    public ClientModVariables getModVariables() {
        return variables;
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> createArgumentBuilder(String name) {
        return ClientCommandManager.literal(name);
    }
}