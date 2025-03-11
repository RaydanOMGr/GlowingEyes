package me.andreasmelone.glowingeyes.fabric;

import me.andreasmelone.forgelikepackets.PacketRegistry;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.fabric.common.GlowingEyesEvents;
import me.andreasmelone.glowingeyes.fabric.common.component.data.PlayerDataComponentImpl;
import me.andreasmelone.glowingeyes.fabric.common.component.eyes.GlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.fabric.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.fabric.common.packet.HasModPacket;
import net.fabricmc.api.ModInitializer;

public class GlowingEyesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        GlowingEyesComponent.setImplementation(new GlowingEyesComponentImpl());
        PlayerDataComponent.setImplementation(new PlayerDataComponentImpl());

        PacketRegistry.INSTANCE.register(
                HasModPacket.ID,
                HasModPacket.class,
                HasModPacket::encode,
                HasModPacket::decode,
                HasModPacket::handle
        );
        PacketRegistry.INSTANCE.register(
                ComponentUpdatePacket.ID,
                ComponentUpdatePacket.class,
                ComponentUpdatePacket::encode,
                ComponentUpdatePacket::decode,
                ComponentUpdatePacket::handle
        );

        GlowingEyesEvents.registerEvents();
    }
}
