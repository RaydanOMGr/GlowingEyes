package me.andreasmelone.glowingeyes.neoforge.client.component.eyes;

import me.andreasmelone.glowingeyes.client.component.eyes.IClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.GlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.neoforge.common.packets.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.neoforge.common.packets.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientGlowingEyesComponentImpl implements IClientGlowingEyesComponent {
    @Override
    public void sendUpdate() {
        Player localPlayer = Minecraft.getInstance().player;
        IGlowingEyes component = ((GlowingEyesComponentImpl) GlowingEyesComponent.getImplementation())
                .getComponent(localPlayer);

        ComponentUpdatePacket packet = new ComponentUpdatePacket(localPlayer.getUUID(), component);
        PacketHandler.send(packet);
    }
}
