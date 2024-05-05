package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import me.andreasmelone.glowingeyes.server.capability.eyes.GlowingEyesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GlowingEyesClientEvents {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            GlowingEyesClient.CLIENT_SCHEDULER.tick();

            Player player = Minecraft.getInstance().player;
            if(player == null) return;

            while(GlowingEyesKeybindings.TOGGLE_MAPPING.consumeClick()) {
                GlowingEyesCapability.setToggledOn(player, !GlowingEyesCapability.isToggledOn(player));
            }
            while(GlowingEyesKeybindings.EYES_EDITOR_MAPPING.consumeClick()) {
                if(Minecraft.getInstance().screen != null) return;
                Minecraft.getInstance().setScreen(new EyesEditorScreen());
            }
        }
    }
}
