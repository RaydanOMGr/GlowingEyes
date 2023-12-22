package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.CapabilityHandler;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import me.andreasmelone.glowingeyes.common.packets.ServerSyncMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;
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

    @SubscribeEvent
    public void on(ClientChatEvent event) {
        if(event.getOriginalMessage().toLowerCase().startsWith("debug ")) {
            String[] args = event.getOriginalMessage().split(" ");
            String arg = args[1];
            if(args.length == 2) {
                EntityPlayer pl = GlowingEyes.proxy.getPlayer().world.getPlayerEntityByName(arg);
                if(pl == null) return;
                IGlowingEyesCapability cap = pl.getCapability(GlowingEyesProvider.CAPABILITY, null);
                if(cap == null) return;
                GlowingEyes.proxy.getPlayer().sendMessage(
                        new TextComponentString(
                            "GlowingEyes data for player " + arg + ": " + cap.getGlowingEyesMap().toString()
                        )
                );
            }
        }
    }
}
