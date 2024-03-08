package me.andreasmelone.glowingeyes.common.capability.data;

import java.util.Set;
import java.util.UUID;

public interface IPlayerData {
    void addTrackingPlayer(UUID uuid);
    void removeTrackingPlayer(UUID uuid);
    Set<UUID> getTrackingPlayers();
}
