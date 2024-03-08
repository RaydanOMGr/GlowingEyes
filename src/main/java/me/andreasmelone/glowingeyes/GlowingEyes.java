package me.andreasmelone.glowingeyes;

import me.andreasmelone.glowingeyes.common.GlowingEyesEvents;
import me.andreasmelone.glowingeyes.common.capability.data.PlayerDataCapability;
import me.andreasmelone.glowingeyes.common.capability.data.PlayerDataHandler;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesHandler;
import me.andreasmelone.glowingeyes.common.packets.PacketManager;
import me.andreasmelone.glowingeyes.common.scheduler.CodeScheduler;
import me.andreasmelone.glowingeyes.common.scheduler.Scheduler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.awt.*;

@Mod(GlowingEyes.MOD_ID)
public class GlowingEyes {
    public static final String MOD_ID = "glowingeyes";
    public static final Color DEFAULT_COLOR = new Color(255, 10, 10, 210);
    private static final Logger LOGGER = LogUtils.getLogger();

    public GlowingEyes() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Glowing Eyes common setup");
        PacketManager.registerAll();

        MinecraftForge.EVENT_BUS.register(PlayerDataHandler.class);
        MinecraftForge.EVENT_BUS.register(GlowingEyesHandler.class);

        MinecraftForge.EVENT_BUS.register(new GlowingEyesEvents());

        MinecraftForge.EVENT_BUS.addListener(GlowingEyesCapability::register);
        MinecraftForge.EVENT_BUS.addListener(PlayerDataCapability::register);
    }
}
