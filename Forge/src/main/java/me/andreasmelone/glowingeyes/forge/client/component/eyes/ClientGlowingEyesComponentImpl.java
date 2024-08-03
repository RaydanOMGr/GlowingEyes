package me.andreasmelone.glowingeyes.forge.client.component.eyes;

import me.andreasmelone.glowingeyes.client.component.eyes.IClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.forge.common.component.eyes.GlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.forge.common.component.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.forge.common.packets.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.forge.common.packets.PacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientGlowingEyesComponentImpl implements IClientGlowingEyesComponent {
    @Override
    public void sendUpdate() {
        Player localPlayer = Minecraft.getInstance().player;
        IGlowingEyes component = ((GlowingEyesComponentImpl) GlowingEyesComponent.getImplementation())
                .getComponent(localPlayer);

        ComponentUpdatePacket packet = new ComponentUpdatePacket(localPlayer, component);
        PacketManager.INSTANCE.sendToServer(packet);
    }
}
