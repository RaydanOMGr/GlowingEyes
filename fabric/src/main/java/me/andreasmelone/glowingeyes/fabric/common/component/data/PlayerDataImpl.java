package me.andreasmelone.glowingeyes.fabric.common.component.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        // we don't want to save this data
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        // we don't want to save this data
    }
}
