package me.andreasmelone.glowingeyes.neoforge.client.component.eyes;

import me.andreasmelone.glowingeyes.client.component.eyes.IClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.neoforge.client.packet.ClientPacketRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientGlowingEyesComponentImpl implements IClientGlowingEyesComponent {
    @Override
    public void sendUpdate() {
        Player localPlayer = Minecraft.getInstance().player;

        ComponentUpdatePacket packet = new ComponentUpdatePacket(localPlayer.getUUID(), GlowingEyesComponent.isToggledOn(localPlayer), GlowingEyesComponent.getGlowingEyesMap(localPlayer));
        ClientPacketRegistrar.send(packet);
    }
}
