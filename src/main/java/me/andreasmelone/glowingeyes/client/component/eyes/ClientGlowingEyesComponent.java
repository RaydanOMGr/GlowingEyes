package me.andreasmelone.glowingeyes.client.component.eyes;

import me.andreasmelone.glowingeyes.client.packet.C2SComponentUpdatePacket;
import me.andreasmelone.glowingeyes.server.component.eyes.GlowingEyesComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientGlowingEyesComponent {
    private ClientGlowingEyesComponent() {
    }

    /**
     * Client-side only method to send an update to the server
     * Throws an exception when ran on the wrong side, so make sure to check the side before calling this method
     */
    public static void sendUpdate() {
        Player localPlayer = Minecraft.getInstance().player;
        new C2SComponentUpdatePacket(localPlayer, GlowingEyesComponent.getComponent(localPlayer)).sendToServer();
    }
}
