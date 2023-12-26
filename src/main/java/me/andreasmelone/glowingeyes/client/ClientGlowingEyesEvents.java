package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.ui.EyesEditScreen;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.eyes.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.ClientCapabilityMessage;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import me.andreasmelone.glowingeyes.common.packets.ServerSyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
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
        if(ClientProxy.toggleKeyBinding.isPressed()) {
            EntityPlayer p = GlowingEyes.proxy.getPlayer();
            IGlowingEyesCapability eyesCapability = p.getCapability(GlowingEyesProvider.CAPABILITY, null);
            if(eyesCapability == null) return;
            eyesCapability.setToggledOn(!eyesCapability.isToggledOn());
            NetworkHandler.sendToServer(new ClientCapabilityMessage(eyesCapability, p));
        }
        if(ClientProxy.eyesEditorKeyBinding.isPressed()) {
            if(Minecraft.getMinecraft().currentScreen != null) return;
            GlowingEyes.proxy.openGui(new EyesEditScreen(Minecraft.getMinecraft()));
        }
    }
}
