package me.andreasmelone.glowingeyes.neoforge.client;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.mod.ClientModVariables;
import me.andreasmelone.glowingeyes.neoforge.client.component.data.ClientPlayerDataComponentImpl;
import me.andreasmelone.glowingeyes.neoforge.client.component.eyes.ClientGlowingEyesComponentImpl;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = GlowingEyes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GlowingEyesClient implements ClientModContext {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ClientModVariables variables = new ClientModVariables();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        GlowingEyesClient glowingEyesClient = new GlowingEyesClient();

        LOGGER.info("Glowing Eyes client setup");
        ClientGlowingEyesComponent.setImplementation(new ClientGlowingEyesComponentImpl());
        ClientPlayerDataComponent.setImplementation(new ClientPlayerDataComponentImpl());

        NeoForge.EVENT_BUS.register(new Commands(glowingEyesClient));
        NeoForge.EVENT_BUS.register(new GlowingEyesClientEvents(glowingEyesClient));

        PresetManager.getInstance().loadPresets();
    }

    @SubscribeEvent
    public static void onRegisterKeyMapping(RegisterKeyMappingsEvent event) {
        GlowingEyesKeybindings.registerBindings(event);
    }

    @Override
    public ClientModVariables getModVariables() {
        return variables;
    }
}