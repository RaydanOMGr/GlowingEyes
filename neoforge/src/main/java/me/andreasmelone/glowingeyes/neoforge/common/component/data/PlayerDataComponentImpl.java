package me.andreasmelone.glowingeyes.neoforge.common.component.data;

import me.andreasmelone.glowingeyes.common.component.data.IPlayerDataComponent;
import me.andreasmelone.glowingeyes.common.packet.HasModPacket;
import me.andreasmelone.glowingeyes.neoforge.common.packets.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;
import java.util.Set;

public class PlayerDataComponentImpl implements IPlayerDataComponent {
    private final IPlayerData localComponent = new PlayerDataImpl();
    private final DeferredHolder<AttachmentType<?>, AttachmentType<IPlayerData>> attachmentType;

    public PlayerDataComponentImpl(DeferredHolder<AttachmentType<?>, AttachmentType<IPlayerData>> attachmentType) {
        this.attachmentType = attachmentType;
    }

    public IPlayerData getComponent(Player player) {
        if(player.isLocalPlayer()) return localComponent;
        return Optional.ofNullable(player.getData(attachmentType))
                .orElseThrow(() -> new IllegalStateException("Could not get PlayerData attachment from player"));
    }

    @Override
    public boolean hasMod(Player player) {
        return getComponent(player).hasMod();
    }

    @Override
    public void setHasMod(Player player, boolean hasMod) {
        getComponent(player).setHasMod(hasMod);
    }

    @Override
    public Set<Player> getTrackedBy(Player player) {
        return getComponent(player).trackedBy();
    }

    @Override
    public void addTrackedBy(Player playerTracked, Player trackedBy) {
        getComponent(playerTracked).addTrackedBy(trackedBy);
    }

    @Override
    public void removeTrackedBy(Player playerTracked, Player trackedBy) {
        getComponent(playerTracked).removeTrackedBy(trackedBy);
    }

    @Override
    public void sendUpdate(ServerPlayer player) {
        PacketHandler.sendTo(player, new HasModPacket());
    }
}
