package me.andreasmelone.glowingeyes.server.component.data;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerDataImpl implements IPlayerData, AutoSyncedComponent {
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

    @Override
    public void readFromNbt(CompoundTag tag) {
        // we don't want to save this data
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        // we don't want to save this data
    }

    @Override
    public boolean shouldSyncWith(ServerPlayer player) {
        // we also don't want to sync this data
        return false;
    }
}
