package me.andreasmelone.glowingeyes.neoforge.client;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.mod.ClientModVariables;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.neoforge.client.component.data.ClientPlayerDataComponentImpl;
import me.andreasmelone.glowingeyes.neoforge.client.component.eyes.ClientGlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.neoforge.client.render.RenderManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

public class GlowingEyesClient implements ClientModContext {
    private static final Logger LOGGER = LogUtils.getLogger();
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

        modEventBus.addListener(RenderManager::onAddLayers);
        NeoForge.EVENT_BUS.register(new Commands(this));
        NeoForge.EVENT_BUS.register(new GlowingEyesClientEvents(this));

        PresetManager.getInstance().loadPresets();
    }

    @SubscribeEvent
    public void onRegisterKeyMapping(RegisterKeyMappingsEvent event) {
        GlowingEyesKeybindings.registerBindings(event);
    }

    @Override
    public ClientModVariables getModVariables() {
        return variables;
    }
}