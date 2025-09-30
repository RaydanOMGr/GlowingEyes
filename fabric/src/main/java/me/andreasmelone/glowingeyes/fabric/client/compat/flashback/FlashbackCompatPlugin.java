package me.andreasmelone.glowingeyes.fabric.client.compat.flashback;

import com.moulberry.flashback.action.ActionRegistry;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.compat.CompatPlugin;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FlashbackCompatPlugin extends CompatPlugin {
    private static final Info INFO = new Info(Util.id(GlowingEyes.MOD_ID, "flashback_compat_plugin"), "Flashback Compatibility Plugin", "1.0.0");

    @Override
    public Info getPluginInfo() {
        return INFO;
    }

    @Override
    public void onRegister() {
        ActionRegistry.register(ActionGlowingEyesUpdate.INSTANCE);
    }

    @Override
    public <T extends CustomPacketPayload> void onPacketSendToServer(T packet) {
        if (packet instanceof ComponentUpdatePacket updatePacket) {
            ActionGlowingEyesUpdate.submitUpdatePacket(updatePacket);
        }
    }
}
