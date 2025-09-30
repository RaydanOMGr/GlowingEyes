package me.andreasmelone.glowingeyes.fabric.client.compat.flashback;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.action.Action;
import com.moulberry.flashback.playback.ReplayPlayer;
import com.moulberry.flashback.playback.ReplayServer;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.packet.ComponentUpdatePacket;
import me.andreasmelone.glowingeyes.common.util.Util;
import me.andreasmelone.glowingeyes.fabric.common.packet.ServerPacketRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ActionGlowingEyesUpdate implements Action {
    private static final ResourceLocation NAME = Util.id(GlowingEyes.MOD_ID, "action/glowing_eyes_update");
    public static final ActionGlowingEyesUpdate INSTANCE = new ActionGlowingEyesUpdate();

    private ActionGlowingEyesUpdate() {
    }

    @Override
    public ResourceLocation name() {
        return NAME;
    }

    @Override
    public void handle(ReplayServer replayServer, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        ComponentUpdatePacket packet = ComponentUpdatePacket.STREAM_CODEC.decode(registryFriendlyByteBuf);

        for (ReplayPlayer replayViewer : replayServer.getReplayViewers()) {
            ServerPacketRegistrar.sendTo(replayViewer, packet);
        }
    }

    public static void submitUpdatePacket(ComponentUpdatePacket packet) {
        Minecraft.getInstance().submit(() -> {
            if (Flashback.RECORDER != null && Flashback.RECORDER.readyToWrite()) {
                Flashback.RECORDER.submitCustomTask(writer -> {
                    writer.startAction(ActionGlowingEyesUpdate.INSTANCE);
                    ComponentUpdatePacket.STREAM_CODEC.encode(writer.friendlyByteBuf(), packet);
                    writer.finishAction(ActionGlowingEyesUpdate.INSTANCE);
                });
            }
        });
    }
}
