package me.andreasmelone.glowingeyes.common.capability.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDataImpl implements IPlayerData {
    Set<UUID> trackingPlayer = new HashSet<>();

    @Override
    public void addTrackingPlayer(UUID uuid) {
        trackingPlayer.add(uuid);
    }

    @Override
    public void removeTrackingPlayer(UUID uuid) {
        trackingPlayer.remove(uuid);
    }

    @Override
    public Set<UUID> getTrackingPlayers() {
        return new HashSet<>(trackingPlayer);
    }
}
