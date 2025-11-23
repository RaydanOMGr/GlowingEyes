package me.andreasmelone.glowingeyes.neoforge.client;

import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.mod.ClientModVariables;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.render.ShaderManager;
import me.andreasmelone.glowingeyes.neoforge.client.compat.NeoForgeCompatPlugins;
import me.andreasmelone.glowingeyes.neoforge.client.component.data.ClientPlayerDataComponentImpl;
import me.andreasmelone.glowingeyes.neoforge.client.component.eyes.ClientGlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.neoforge.client.render.RenderManager;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GlowingEyesClient implements ClientModContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlowingEyesClient.class);
    private final ClientModVariables variables = new ClientModVariables();
    private final IEventBus modEventBus;

    public GlowingEyesClient(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Glowing Eyes client setup");
        ClientGlowingEyesComponent.setImplementation(new ClientGlowingEyesComponentImpl());
        ClientPlayerDataComponent.setImplementation(new ClientPlayerDataComponentImpl());

        this.modEventBus.addListener(RenderManager::onAddLayers);
        NeoForge.EVENT_BUS.register(new Commands(this));
        NeoForge.EVENT_BUS.register(new GlowingEyesClientEvents(this));

        NeoForgeCompatPlugins.init();

        PresetManager.getInstance().loadPresets();
    }

    @SubscribeEvent
    public void onRegisterShaders(RegisterShadersEvent event) {
        ShaderManager.register((id, format, consumer) -> {
            try {
                event.registerShader(
                        new ShaderInstance(event.getResourceProvider(), id, format),
                        consumer
                );
            } catch (IOException e) {
                LOGGER.error("Failed to register shader {}!", id, e);
            }
        });
    }

    @SubscribeEvent
    public void onRegisterKeyMapping(RegisterKeyMappingsEvent event) {
        GlowingEyesKeybindings.registerBindings(event);
    }

    @Override
    public ClientModVariables getModVariables() {
        return this.variables;
    }
}