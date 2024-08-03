package me.andreasmelone.glowingeyes.fabric.common.component.data;

import me.andreasmelone.glowingeyes.common.component.data.IPlayerDataComponent;
import me.andreasmelone.glowingeyes.fabric.common.component.ComponentHandler;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class PlayerDataComponentImpl implements IPlayerDataComponent {
    private final IPlayerData localComponent = new PlayerDataImpl();

    public IPlayerData getComponent(Player player) {
        if(player.isLocalPlayer()) return localComponent;
        return ComponentHandler.PLAYER_DATA.get(player);
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
}
