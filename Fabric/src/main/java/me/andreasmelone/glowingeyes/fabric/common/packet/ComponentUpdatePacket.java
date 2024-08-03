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
import java.util.function.Supplier;

public class ComponentUpdatePacket {
    public static final ResourceLocation ID = new ResourceLocation(GlowingEyes.MOD_ID, "capability_update");

    UUID playerUUID;
    Player player;
    IGlowingEyes glowingEyes;

    public ComponentUpdatePacket(Player player, IGlowingEyes glowingEyes) {
        this.player = player;
        this.glowingEyes = glowingEyes == null ? new GlowingEyesImpl() : glowingEyes;
    }

    private ComponentUpdatePacket(UUID playerUUID, IGlowingEyes glowingEyes) {
        this.playerUUID = playerUUID;
        this.glowingEyes = glowingEyes == null ? new GlowingEyesImpl() : glowingEyes;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(player.getUUID());
        buffer.writeBoolean(glowingEyes.isToggledOn());
        buffer.writeByteArray(Util.serializeMap(glowingEyes.getGlowingEyesMap()));
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
                GlowingEyesComponent.setGlowingEyesMap(target, this.glowingEyes.getGlowingEyesMap());
                GlowingEyesComponent.setToggledOn(target, this.glowingEyes.isToggledOn());
            } else {
                MinecraftServer server = ctx.getSender().getServer();
                if (server == null) return;

                Player target = server.getPlayerList().getPlayer(this.playerUUID);
                if (target == null) return;
                GlowingEyesComponent.setGlowingEyesMap(target, this.glowingEyes.getGlowingEyesMap());
                GlowingEyesComponent.setToggledOn(target, this.glowingEyes.isToggledOn());

                for (ServerPlayer serverPlayer : PlayerLookup.tracking(target)) {
                    if (serverPlayer == target) return;
                    ComponentUpdatePacket newPacket = new ComponentUpdatePacket(target, this.glowingEyes);
                    PacketRegistry.INSTANCE.sendTo(serverPlayer, ID, newPacket);
                }
            }
        });
    }
}
