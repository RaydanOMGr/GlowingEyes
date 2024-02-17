package me.andreasmelone.glowingeyes.common.capability.eyes;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class GlowingEyesCapability {

    public static final Capability<IGlowingEyes> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IGlowingEyes.class);
    }

    private static final IGlowingEyes localCapability = new GlowingEyesImpl();
    public static IGlowingEyes getCapability(Player player) {
        return player.getCapability(INSTANCE)
                .orElseThrow(() -> new IllegalStateException("Could not get GlowingEyes capability from player"));
    }

    private GlowingEyesCapability() {
    }
}