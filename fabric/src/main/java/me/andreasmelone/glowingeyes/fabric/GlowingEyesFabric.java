package me.andreasmelone.glowingeyes.fabric;

import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.fabric.common.GlowingEyesEvents;
import me.andreasmelone.glowingeyes.fabric.common.component.data.PlayerDataComponentImpl;
import me.andreasmelone.glowingeyes.fabric.common.component.eyes.GlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.fabric.common.packet.PacketHandler;
import net.fabricmc.api.ModInitializer;

public class GlowingEyesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        GlowingEyesComponent.setImplementation(new GlowingEyesComponentImpl());
        PlayerDataComponent.setImplementation(new PlayerDataComponentImpl());

        PacketHandler.init();
        GlowingEyesEvents.registerEvents();
    }
}
