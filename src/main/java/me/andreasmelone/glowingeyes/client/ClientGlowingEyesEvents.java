package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.ui.ColorPickerScreen;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import me.andreasmelone.glowingeyes.common.packets.ServerSyncMessage;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
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

    @SubscribeEvent(receiveCanceled=true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(ClientProxy.editorKeybind.isPressed()) {
            GlowingEyes.proxy.openGui(new ColorPickerScreen());
        } else if(ClientProxy.toggleKeybind.isPressed()) {
            // TODO: Implement this
        }
    }
}
