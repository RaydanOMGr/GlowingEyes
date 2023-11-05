package me.andreasmelone.glowingeyes.client;

import javafx.scene.effect.Glow;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.data.ByteArray;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import me.andreasmelone.glowingeyes.common.packets.ServerSyncMessage;
import me.andreasmelone.glowingeyes.common.scheduler.ScheduledTask;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientGlowingEyesEvents {
    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if(event.getEntity() == GlowingEyes.proxy.getPlayer()) {
            if(GlowingEyes.serverHasMod) return;
            NetworkHandler.sendToServer(new ServerSyncMessage());
        }
    }

    @SubscribeEvent
    public void onPlayerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        GlowingEyes.serverHasMod = false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            GlowingEyes.proxy.getScheduler().tick();
        }
    }
}
