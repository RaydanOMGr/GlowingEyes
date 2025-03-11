package me.andreasmelone.glowingeyes.fabric.client.component.data;

import me.andreasmelone.forgelikepackets.PacketRegistry;
import me.andreasmelone.glowingeyes.client.component.data.IClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.fabric.common.packet.HasModPacket;

public class ClientPlayerDataComponentImpl implements IClientPlayerDataComponent {
    @Override
    public void sendRequest() {
        HasModPacket packet = new HasModPacket();
        PacketRegistry.INSTANCE.sendToServer(HasModPacket.ID, packet);
    }
}
