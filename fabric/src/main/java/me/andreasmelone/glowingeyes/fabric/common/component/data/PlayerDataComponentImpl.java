package me.andreasmelone.glowingeyes.fabric.common.component.data;

import me.andreasmelone.glowingeyes.common.component.data.IPlayerDataComponent;
import me.andreasmelone.glowingeyes.common.packet.HasModPacket;
import me.andreasmelone.glowingeyes.fabric.common.component.ComponentHandler;
import me.andreasmelone.glowingeyes.fabric.common.packet.ServerPacketRegistrar;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class PlayerDataComponentImpl implements IPlayerDataComponent {
    private final IPlayerData localComponent = new PlayerDataImpl();

    public IPlayerData getComponent(Player player) {
        if(player.isLocalPlayer()) return this.localComponent;
        return ComponentHandler.PLAYER_DATA.get(player);
    }

    @Override
    public boolean hasMod(Player player) {
        return this.getComponent(player).hasMod();
    }

    @Override
    public void setHasMod(Player player, boolean hasMod) {
        this.getComponent(player).setHasMod(hasMod);
    }

    @Override
    public Set<Player> getTrackedBy(Player player) {
        return this.getComponent(player).trackedBy();
    }

    @Override
    public void addTrackedBy(Player playerTracked, Player trackedBy) {
        this.getComponent(playerTracked).addTrackedBy(trackedBy);
    }

    @Override
    public void removeTrackedBy(Player playerTracked, Player trackedBy) {
        this.getComponent(playerTracked).removeTrackedBy(trackedBy);
    }

    @Override
    public void sendUpdate(ServerPlayer player) {
        ServerPacketRegistrar.sendTo(player, new HasModPacket());
    }
}
