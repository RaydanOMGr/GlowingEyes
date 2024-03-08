package me.andreasmelone.glowingeyes.common.capability.data;

import java.util.Set;
import java.util.UUID;

public interface IPlayerData {
    boolean hasMod();
    void setHasMod(boolean hasMod);
    void addTrackingPlayer(UUID uuid);
    void removeTrackingPlayer(UUID uuid);
    Set<UUID> getTrackingPlayers();
}
