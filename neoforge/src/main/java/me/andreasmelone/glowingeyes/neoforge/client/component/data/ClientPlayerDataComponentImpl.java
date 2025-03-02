package me.andreasmelone.glowingeyes.neoforge.client.component.data;

import me.andreasmelone.glowingeyes.client.component.data.IClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.neoforge.common.packets.HasModPacket;
import me.andreasmelone.glowingeyes.neoforge.common.packets.PacketHandler;

public class ClientPlayerDataComponentImpl implements IClientPlayerDataComponent {
    @Override
    public void sendRequest() {
        HasModPacket packet = new HasModPacket();
        PacketHandler.send(packet);
    }
}
