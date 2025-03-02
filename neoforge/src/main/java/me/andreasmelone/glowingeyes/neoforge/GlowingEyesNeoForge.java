package me.andreasmelone.glowingeyes.neoforge;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.neoforge.client.render.RenderManager;
import me.andreasmelone.glowingeyes.neoforge.common.GlowingEyesEvents;
import me.andreasmelone.glowingeyes.neoforge.common.component.ComponentHandler;
import me.andreasmelone.glowingeyes.neoforge.common.packets.PacketHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(GlowingEyes.MOD_ID)
public class GlowingEyesNeoForge {
    private static final Logger LOGGER = LogUtils.getLogger();

    public GlowingEyesNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(RenderManager::onAddLayers);
        modEventBus.addListener(PacketHandler::registerPackets);

        ComponentHandler.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Glowing Eyes common setup");
        NeoForge.EVENT_BUS.register(new GlowingEyesEvents());
    }
}
