package me.andreasmelone.glowingeyes.client;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.render.RenderManager;
import me.andreasmelone.glowingeyes.common.scheduler.CodeScheduler;
import me.andreasmelone.glowingeyes.common.scheduler.Scheduler;
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
    protected static final Scheduler CLIENT_SCHEDULER = new CodeScheduler();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        LOGGER.info("Glowing Eyes client setup");

        modEventBus.addListener(RenderManager::onAddLayers);

        MinecraftForge.EVENT_BUS.register(Commands.class);
        MinecraftForge.EVENT_BUS.register(new GlowingEyesClientEvents());

        PresetManager.getInstance().loadPresets();
    }

    @SubscribeEvent
    public static void onRegisterKeyMapping(RegisterKeyMappingsEvent event) {
        GlowingEyesKeybindings.registerBindings(event);
    }

    public static Scheduler getClientScheduler() {
        return CLIENT_SCHEDULER;
    }
}