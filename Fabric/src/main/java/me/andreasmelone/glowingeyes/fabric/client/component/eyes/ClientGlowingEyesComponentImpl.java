package me.andreasmelone.glowingeyes.fabric.client.component.eyes;

import me.andreasmelone.forgelikepackets.PacketRegistry;
import me.andreasmelone.glowingeyes.client.component.eyes.IClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.fabric.common.component.eyes.GlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.fabric.common.component.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.fabric.common.packet.ComponentUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientGlowingEyesComponentImpl implements IClientGlowingEyesComponent {
    @Override
    public void sendUpdate() {
        Player localPlayer = Minecraft.getInstance().player;
        IGlowingEyes component = ((GlowingEyesComponentImpl) GlowingEyesComponent.getImplementation())
                .getComponent(localPlayer);

        ComponentUpdatePacket packet = new ComponentUpdatePacket(localPlayer, component);
        PacketRegistry.INSTANCE.sendToServer(ComponentUpdatePacket.ID, packet);
    }
}
