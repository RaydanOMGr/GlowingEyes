package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.common.capability.data.PlayerDataCapability;
import me.andreasmelone.glowingeyes.common.packets.HasModPacket;
import me.andreasmelone.glowingeyes.common.packets.PacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GlowingEyesClientEvents {
    @SubscribeEvent
    public void onPlayerJoinLevel(EntityJoinLevelEvent event) {
        if(event.getEntity() == Minecraft.getInstance().player) {
            if(PlayerDataCapability.hasMod((Player) event.getEntity())) return;
            PacketManager.INSTANCE.sendToServer(new HasModPacket());
        }
    }

    @SubscribeEvent
    public void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerDataCapability.setHasMod(event.getEntity(), false);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            GlowingEyesClient.CLIENT_SCHEDULER.tick();
        }
    }
}
