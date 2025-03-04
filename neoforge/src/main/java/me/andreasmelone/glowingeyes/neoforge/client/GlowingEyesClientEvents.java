package me.andreasmelone.glowingeyes.neoforge.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class GlowingEyesClientEvents {
    private final ClientModContext mod;

    public GlowingEyesClientEvents(ClientModContext mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        GlowingEyes.SCHEDULER_CLIENT.tick();

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        while (GlowingEyesKeybindings.TOGGLE_MAPPING.consumeClick()) {
            GlowingEyesComponent.setToggledOn(player, !GlowingEyesComponent.isToggledOn(player));
        }
        while (GlowingEyesKeybindings.EYES_EDITOR_MAPPING.consumeClick()) {
            if (Minecraft.getInstance().screen != null) return;
            Minecraft.getInstance().setScreen(new EyesEditorScreen(mod));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientPlayerDataComponent.sendRequest();
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        DynamicTextureCache.clear();
    }
}
