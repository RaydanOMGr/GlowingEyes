package me.andreasmelone.glowingeyes.forge.client.component.data;

import me.andreasmelone.glowingeyes.client.component.data.IClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.forge.common.packets.HasModPacket;
import me.andreasmelone.glowingeyes.forge.common.packets.PacketManager;

public class ClientPlayerDataComponentImpl implements IClientPlayerDataComponent {
    @Override
    public void sendRequest() {
        HasModPacket packet = new HasModPacket();
        PacketManager.INSTANCE.sendToServer(packet);
    }
}
