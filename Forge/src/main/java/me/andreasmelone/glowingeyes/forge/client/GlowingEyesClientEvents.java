package me.andreasmelone.glowingeyes.forge.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GlowingEyesClientEvents {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            GlowingEyes.SCHEDULER_CLIENT.tick();

            Player player = Minecraft.getInstance().player;
            if(player == null) return;

            while(GlowingEyesKeybindings.TOGGLE_MAPPING.consumeClick()) {
                GlowingEyesComponent.setToggledOn(player, !GlowingEyesComponent.isToggledOn(player));
            }
            while(GlowingEyesKeybindings.EYES_EDITOR_MAPPING.consumeClick()) {
                if(Minecraft.getInstance().screen != null) return;
                Minecraft.getInstance().setScreen(new EyesEditorScreen());
            }
        }
    }
}
