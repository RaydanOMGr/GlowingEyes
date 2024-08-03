package me.andreasmelone.glowingeyes.forge.common.component.data;

import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerDataImpl implements IPlayerData {
    private boolean hasMod = false;
    private final Set<Player> trackedBy = new HashSet<>();

    @Override
    public boolean hasMod() {
        return hasMod;
    }

    @Override
    public void setHasMod(boolean hasMod) {
        this.hasMod = hasMod;
    }

    @Override
    public Set<Player> trackedBy() {
        return new HashSet<>(trackedBy);
    }

    @Override
    public void addTrackedBy(Player player) {
        trackedBy.add(player);
    }

    @Override
    public void removeTrackedBy(Player player) {
        trackedBy.remove(player);
    }
}
