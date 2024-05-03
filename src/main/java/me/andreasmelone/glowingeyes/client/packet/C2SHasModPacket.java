package me.andreasmelone.glowingeyes.client.packet;

import me.andreasmelone.glowingeyes.server.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.server.packet.S2CHasModPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public class C2SHasModPacket extends S2CHasModPacket {
    public static void sendToServer() {
        ClientPlayNetworking.send(ID, PacketByteBufs.create());
    }

    public static void registerHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(S2CHasModPacket.ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                PlayerDataComponent.setHasMod(client.player, true);
            });
        });
    }
}
