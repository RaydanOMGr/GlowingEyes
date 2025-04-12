package me.andreasmelone.glowingeyes.fabric.common.packet;

import me.andreasmelone.forgelikepackets.PacketContext;
import me.andreasmelone.forgelikepackets.PacketRegistry;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Util;
import me.andreasmelone.glowingeyes.fabric.common.component.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.fabric.common.component.eyes.IGlowingEyes;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ComponentUpdatePacket {
    public static final ResourceLocation ID = new ResourceLocation(GlowingEyes.MOD_ID, "capability_update");

    public UUID playerUUID;
    public IGlowingEyes capability;

    public ComponentUpdatePacket(Player player, IGlowingEyes capability) {
        this.playerUUID = player.getUUID();
        this.capability = capability == null ? new GlowingEyesImpl() : capability;
    }

    private ComponentUpdatePacket(UUID playerUUID, IGlowingEyes capability) {
        this.playerUUID = playerUUID;
        this.capability = capability == null ? new GlowingEyesImpl() : capability;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerUUID);
        buffer.writeBoolean(capability.isToggledOn());
        buffer.writeByteArray(Util.serializeMap(capability.getGlowingEyesMap()));
    }

    public static ComponentUpdatePacket decode(FriendlyByteBuf buffer) {
        UUID playerUUID = buffer.readUUID();
        boolean toggledOn = buffer.readBoolean();
        byte[] glowingEyesMap = buffer.readByteArray();

        IGlowingEyes capability = new GlowingEyesImpl();
        capability.setToggledOn(toggledOn);
        capability.setGlowingEyesMap(Util.deserializeMap(glowingEyesMap));

        return new ComponentUpdatePacket(playerUUID, capability);
    }

    public void handle(PacketContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.getDirection() == PacketFlow.CLIENTBOUND) {
                Player target = Minecraft.getInstance().level.getPlayerByUUID(this.playerUUID);
                if (target == null) {
                    return;
                }
                GlowingEyesComponent.setGlowingEyesMap(target, this.capability.getGlowingEyesMap());
                GlowingEyesComponent.setToggledOn(target, this.capability.isToggledOn());
            } else {
                MinecraftServer server = ctx.getSender().getServer();
                if (server == null) return;

                Player target = server.getPlayerList().getPlayer(this.playerUUID);
                if (target == null) return;
                GlowingEyesComponent.setGlowingEyesMap(target, this.capability.getGlowingEyesMap());
                GlowingEyesComponent.setToggledOn(target, this.capability.isToggledOn());

                for (ServerPlayer serverPlayer : PlayerLookup.tracking(target)) {
                    if (serverPlayer == target) return;
                    ComponentUpdatePacket newPacket = new ComponentUpdatePacket(target, this.capability);
                    PacketRegistry.INSTANCE.sendTo(serverPlayer, ID, newPacket);
                }
            }
        });
    }
}
