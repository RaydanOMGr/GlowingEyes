package me.andreasmelone.glowingeyes.fabric.client.component.eyes;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.component.eyes.IClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.fabric.client.packet.ClientPacketRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientGlowingEyesComponentImpl implements IClientGlowingEyesComponent {
    @Override
    public void sendUpdate() {
        Player localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            LogUtils.getLogger().debug("Failed to retrieve local player!");
            return;
        }

        ComponentUpdatePacket packet = new ComponentUpdatePacket(localPlayer.getUUID(), GlowingEyesComponent.isToggledOn(localPlayer), GlowingEyesComponent.getGlowingEyesMap(localPlayer));
        ClientPacketRegistrar.send(packet);
    }
}
