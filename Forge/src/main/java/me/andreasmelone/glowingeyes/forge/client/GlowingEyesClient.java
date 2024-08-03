package me.andreasmelone.glowingeyes.forge.client;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.forge.client.component.data.ClientPlayerDataComponentImpl;
import me.andreasmelone.glowingeyes.forge.client.component.eyes.ClientGlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.forge.client.render.RenderManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = GlowingEyes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GlowingEyesClient {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        LOGGER.info("Glowing Eyes client setup");
        ClientGlowingEyesComponent.setImplementation(new ClientGlowingEyesComponentImpl());
        ClientPlayerDataComponent.setImplementation(new ClientPlayerDataComponentImpl());

        modEventBus.addListener(RenderManager::onAddLayers);

        MinecraftForge.EVENT_BUS.register(Commands.class);
        MinecraftForge.EVENT_BUS.register(new GlowingEyesClientEvents());

        PresetManager.getInstance().loadPresets();
    }

    @SubscribeEvent
    public static void onRegisterKeyMapping(RegisterKeyMappingsEvent event) {
        GlowingEyesKeybindings.registerBindings(event);
    }
}