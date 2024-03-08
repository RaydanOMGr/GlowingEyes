package me.andreasmelone.glowingeyes.common;

import me.andreasmelone.glowingeyes.common.capability.data.PlayerDataCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GlowingEyesEvents {
    @SubscribeEvent
    public void onPlayerExit(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerDataCapability.removePlayerWithMod(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        System.out.println("Player start tracking");
        if(!(event.getTarget() instanceof Player target)) return;
        System.out.println("Target is player");
        if(!PlayerDataCapability.hasMod(target)) return;
        System.out.println("Target has mod");

        PlayerDataCapability.addTrackingPlayer(event.getEntity(), target.getUUID());
    }

    @SubscribeEvent
    public void onPlayerStopTracking(PlayerEvent.StopTracking event) {
        System.out.println("Player stop tracking");
        if(!(event.getTarget() instanceof Player target)) return;
        System.out.println("Target is player");
        if(!PlayerDataCapability.hasMod(target)) return;
        System.out.println("Target has mod");

        PlayerDataCapability.removeTrackingPlayer(event.getEntity(), target.getUUID());
    }
}
