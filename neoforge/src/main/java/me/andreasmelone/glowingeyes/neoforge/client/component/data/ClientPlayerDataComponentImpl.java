package me.andreasmelone.glowingeyes.neoforge.client.component.data;

import me.andreasmelone.glowingeyes.client.component.data.IClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.common.packet.HasModPacket;
import me.andreasmelone.glowingeyes.neoforge.client.packet.ClientPacketRegistrar;

public class ClientPlayerDataComponentImpl implements IClientPlayerDataComponent {
    boolean isModOnServer = false;

    @Override
    public void sendRequest() {
        HasModPacket packet = new HasModPacket();
        ClientPacketRegistrar.send(packet);
    }

    @Override
    public void setIsModOnServer(boolean isModOnServer) {
        this.isModOnServer = isModOnServer;
    }

    @Override
    public boolean isModOnServer() {
        return this.isModOnServer;
    }
}
