package me.andreasmelone.glowingeyes.forge;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.forge.common.GlowingEyesEvents;
import me.andreasmelone.glowingeyes.forge.common.component.data.PlayerDataComponentImpl;
import me.andreasmelone.glowingeyes.forge.common.component.data.PlayerDataHandler;
import me.andreasmelone.glowingeyes.forge.common.component.eyes.GlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.forge.common.component.eyes.GlowingEyesHandler;
import me.andreasmelone.glowingeyes.forge.common.packets.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.forge.common.packets.HasModPacket;
import me.andreasmelone.glowingeyes.forge.common.packets.PacketManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(GlowingEyes.MOD_ID)
public class GlowingEyesForge {
    private static final Logger LOGGER = LogUtils.getLogger();

    public GlowingEyesForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Glowing Eyes common setup");
        GlowingEyesComponent.setImplementation(new GlowingEyesComponentImpl());
        PlayerDataComponent.setImplementation(new PlayerDataComponentImpl());
        registerPackets();

        MinecraftForge.EVENT_BUS.register(PlayerDataHandler.class);
        MinecraftForge.EVENT_BUS.register(GlowingEyesHandler.class);

        MinecraftForge.EVENT_BUS.register(new GlowingEyesEvents());

        MinecraftForge.EVENT_BUS.addListener(GlowingEyesComponentImpl::register);
        MinecraftForge.EVENT_BUS.addListener(PlayerDataComponentImpl::register);
    }

    private void registerPackets() {
        PacketManager.register(
                ComponentUpdatePacket.class,
                ComponentUpdatePacket::encode,
                ComponentUpdatePacket::decode,
                ComponentUpdatePacket::messageConsumer
        );
        PacketManager.register(
                HasModPacket.class,
                HasModPacket::encode,
                HasModPacket::decode,
                HasModPacket::messageConsumer
        );
    }
}
