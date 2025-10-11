package me.andreasmelone.glowingeyes.fabric.client.compat.replaymod;

import com.replaymod.recording.handler.RecordingEventHandler;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.compat.CompatPlugin;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ReplayModCompatPlugin extends CompatPlugin {
    public static final Info INFO = new Info(Util.id(GlowingEyes.MOD_ID, "replaymod_compat_plugin"), "ReplayMod Compat Plugin", "1.0.0");
    @SuppressWarnings("unused")
    private final RecordingEventHandler handler = this.getRecordingEventHandler(); // this field is never used,
    // but it makes the class throw an exception when loaded forcing the plugin not to get registered

    @Override
    public Info getPluginInfo() {
        return INFO;
    }

    @Override
    public <T extends CustomPacketPayload> void onPacketSendToServer(T packet) {
        if(packet instanceof ComponentUpdatePacket) {
            RecordingEventHandler handler = this.getRecordingEventHandler();
            if(handler != null) {
                handler.onPacket(new ClientboundCustomPayloadPacket(packet));
            }
        }
    }

    public RecordingEventHandler getRecordingEventHandler() {
        return ((RecordingEventHandler.RecordingEventSender) Minecraft.getInstance().levelRenderer).getRecordingEventHandler();
    }
}
