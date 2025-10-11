package me.andreasmelone.glowingeyes.neoforge.common.component.data;

import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerDataImpl implements IPlayerData {
    private boolean hasMod = false;
    private final Set<Player> trackedBy = new HashSet<>();

    @Override
    public boolean hasMod() {
        return this.hasMod;
    }

    @Override
    public void setHasMod(boolean hasMod) {
        this.hasMod = hasMod;
    }

    @Override
    public Set<Player> trackedBy() {
        return new HashSet<>(this.trackedBy);
    }

    @Override
    public void addTrackedBy(Player player) {
        this.trackedBy.add(player);
    }

    @Override
    public void removeTrackedBy(Player player) {
        this.trackedBy.remove(player);
    }
}
